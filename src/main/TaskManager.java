package main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * A simple graphical user interface (GUI) to interact with {@code TaskUIManager}. 
 * <p>
 * {@code TaskUIManagerUI} includes a sortable TaskUI list displaying all TaskUIs with their details 
 * (title, description, due date, and whether it's completed). Click the column header to sort
 * alphabetically, click again for more options. Shift clicking sets secondary sorting, e.g. 
 * clicking "Is Completed?" then shift clicking "Due Date" will sort by completion then date.
 */
public class TaskManager extends Application {
    private static final Font HEADER_FONT = new Font("Arial", 19);
    private static final int TABLE_WIDTH = 1280;
    private static final int TABLE_HEIGHT = 720;
    private static final int PADDING = 10;
    private static final String TITLE_HEADER = "Title";
    private static final String DESCRIPTION_HEADER = "Description";
    private static final String DUE_DATE_HEADER = "Due Date";
    private static final String IS_COMPLETED_HEADER = "Completed?";
    private static final int TITLE_COLUMN_MAX_WIDTH = 200;
    private static final int DUE_DATE_COLUMN_MAX_WIDTH = 165;
    private static final int COMPLETE_MAX_WIDTH = 100;
    private static final int DESCRIPTION_FIELD_WIDTH = TABLE_WIDTH
            - TITLE_COLUMN_MAX_WIDTH - DUE_DATE_COLUMN_MAX_WIDTH - COMPLETE_MAX_WIDTH;
    private static final String TITLE_ERROR_MESSAGE = "Please enter a title.";
    private static final String DUE_DATE_ERROR_MESSAGE = 
            "Please use the form:\nmonth/day/year\n.e.g. 8/10/2023";

    /**
     * Launches the window, causing the creation and showing of the UI.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates and shows the UI, causing it to appear on the screen to be used.
     */ 
    @Override
    public void start(Stage stage) {
        stage.setTitle("Task Manager");

        // Label the table, TaskUIs are here
        final Label label = new Label("Tasks");
        label.setFont(HEADER_FONT);
        // center label
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        // build the main table
        final TableView<TaskUI> table = new TableView<TaskUI>();
        table.setPrefWidth(TABLE_WIDTH);
        table.setPrefHeight(TABLE_HEIGHT);
        // Columns fill the width of the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // dummy example data
        ObservableList<TaskUI> data = FXCollections.observableArrayList(
            new TaskUI("b", "b", null, false),
            new TaskUI("a", "a", null, false),
            new TaskUI("c", "c", null, true)
        );
        table.setItems(data);

        // Add the table columns
        List<TableColumn<TaskUI, String>> columns = createColumns(stage, data);
        table.getColumns().addAll(columns);

        // Add button for TaskUI creation
        Button addButton = createAddButton(stage, data);

        // Vertically stack the elements
        final VBox vBox = new VBox(PADDING, label, table, addButton);
        vBox.setPadding(new Insets(PADDING));
        
        // Container enabling user interactions
        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        // display window
        stage.show();
    }

    /**
     * Returns a List of all the columns in the table of TaskUIs. Columns are title, description, 
     * due date, and completed?. Columns can be edited by double clicking.
     */ 
    private static List<TableColumn<TaskUI, String>> createColumns(Stage ownerStage, 
            ObservableList<TaskUI> data) {
        List<TableColumn<TaskUI, String>> columns = new ArrayList<TableColumn<TaskUI, String>>();

        // Title
        TableColumn<TaskUI, String> titleColumn = new TableColumn<TaskUI, String>(TITLE_HEADER);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(new TaskCellFactory(ownerStage, data));
        titleColumn.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
        columns.add(titleColumn);

        // Description
        TableColumn<TaskUI, String> descriptionColumn = 
                new TableColumn<TaskUI, String>(DESCRIPTION_HEADER);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(new TaskCellFactory(ownerStage, data));
        // width = as big as it can be
        columns.add(descriptionColumn);

        // Due Date
        TableColumn<TaskUI, String> dueDateColumn =
                new TableColumn<TaskUI, String>(DUE_DATE_HEADER);
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDateAsString"));
        dueDateColumn.setCellFactory(new TaskCellFactory(ownerStage, data));
        // Sort chronologically
        dueDateColumn.setComparator(new Comparator<String>() {
            private static final DueDateConverter converter = new DueDateConverter();

            @Override
            public int compare(String dateString1, String dateString2) {
                // non-null Strings

                if (dateString1.isBlank() || dateString2.isBlank()) {
                    // no date, string sort
                    return dateString1.compareTo(dateString2);
                }
                
                LocalDate date1 = converter.fromString(dateString1);
                LocalDate date2 = converter.fromString(dateString2);

                return date1.compareTo(date2);
            }
        });
        dueDateColumn.setMaxWidth(DUE_DATE_COLUMN_MAX_WIDTH);
        columns.add(dueDateColumn);

        // Complete? column
        TableColumn<TaskUI, String> completeColumn =
                new TableColumn<TaskUI, String>(IS_COMPLETED_HEADER);
        completeColumn.setCellValueFactory(new PropertyValueFactory<>("isCompletedAsString"));
        completeColumn.setCellFactory(new TaskCellFactory(ownerStage, data));
        completeColumn.setMaxWidth(COMPLETE_MAX_WIDTH);
        columns.add(completeColumn);

        return columns;
    }

    /**
     * Creates an add button triggering a TaskUI creation window upon press. 
     * <p>
     * The TaskUI creation window has inputs for title, description, due date, and completed?. 
     * There must be a title and due date is flexible (no date, only time; no time, only date).
     * @param ownerStage {@code Stage} this button will be for
     * @param data The Table's data this button will add to
     * @return The add button
     */
    private static Button createAddButton(Stage ownerStage, ObservableList<TaskUI> data) {
        // Create an input submition button 
        final Button addButton = new Button("Add");

        addButton.setOnAction((ActionEvent addEvent) -> {
            // Prep the window
            final Stage stage = new Stage();
            stage.setTitle("Create Task");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerStage);

            // title input
            final Label titleLabel = new Label(TITLE_HEADER);
            titleLabel.setFont(HEADER_FONT);
            final TextArea titleField = new TextArea();
            titleField.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
            titleField.setPrefHeight(0);

            // description input
            final Label descriptionLabel = new Label(DESCRIPTION_HEADER);
            descriptionLabel.setFont(HEADER_FONT);
            final TextArea descriptionField = new TextArea();
            descriptionField.setPrefWidth(DESCRIPTION_FIELD_WIDTH);
            
            // due date input
            final Label dueDateLabel = new Label(DUE_DATE_HEADER);
            dueDateLabel.setFont(HEADER_FONT);
            final DatePicker datePicker = new DatePicker();
            DueDateConverter converter = new DueDateConverter();
            datePicker.setConverter(converter);

            // completed? input
            final Label isCompletedLabel = new Label(IS_COMPLETED_HEADER);
            isCompletedLabel.setFont(HEADER_FONT);
            final CheckBox isCompletedCheckBox = new CheckBox();

            // create a TaskUI on button press, prompt for title when empty
            final Button createButton = new Button("Create");
            createButton.setOnAction((ActionEvent createEvent) -> {
                String title = titleField.getText();

                if (title.trim().isEmpty()) {
                    displayError(stage, TITLE_ERROR_MESSAGE);

                    titleField.requestFocus();
                } else if (converter.getStatus() == DueDateConverter.STATUS.SUCCESS) {
                    String description = descriptionField.getText();
                    LocalDate dueDate = datePicker.getValue();
                    boolean isCompleted = isCompletedCheckBox.isSelected();
                    
                    data.add(new TaskUI(title, description, dueDate, isCompleted));

                    stage.hide();
                } else {
                    displayError(stage, DUE_DATE_ERROR_MESSAGE);
                    
                    datePicker.requestFocus();
                    datePicker.getEditor().selectAll();
                }
            });

            // A way to back out for the user
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction((ActionEvent cancelEvent) -> {
                stage.hide();
            });

            // Button row, "Create" next to "Cancel"
            HBox buttonsHBox = new HBox(PADDING / 2, createButton, cancelButton);
            buttonsHBox.setPadding(new Insets(2 * PADDING, 0, 0, 0));

            // Vertically stack the inputs and button row
            VBox vBox = new VBox(PADDING / 2, titleLabel, titleField, descriptionLabel,
                    descriptionField, dueDateLabel, datePicker, isCompletedLabel,
                    isCompletedCheckBox, buttonsHBox);
            vBox.setPadding(new Insets(PADDING));

            // Container enabling user interactions
            Scene dialogScene = new Scene(vBox);
            stage.setScene(dialogScene);

            // display window
            stage.show();
        });

        return addButton;
    }

    /**
     * Displays specified text in a new window anchored to specified {@code Stage}. Intended for 
     * users to read the text then press the "Okay" button to return with the information.
     * 
     * @param ownerStage owner of the stage to be created.
     * @param text information to be displayed as a centerpiece.
     */
    private static void displayError(Stage ownerStage, String text) {
        // Prep window
        Stage stage = new Stage();
        stage.setTitle("Error!");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerStage);

        // Label explaining to user
        final Label label = new Label(text);
        label.setFont(new Font("Arial", 19));
        label.setTextAlignment(TextAlignment.CENTER);

        // Button to exit
        final Button button = new Button("Okay");
        button.setOnAction((ActionEvent createEvent) -> {
            stage.hide();
        });

        // Vertically stack the components and center
        double centeringPadding = 4 * PADDING;
        VBox vBox = new VBox(centeringPadding, label, button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(centeringPadding, PADDING, PADDING, PADDING));

        // Container enabling user interactions
        Scene dialogScene = new Scene(vBox);
        stage.setScene(dialogScene);
        
        // display window
        stage.show();
    }

    /**
     * Facilitates non-String {@code Task} data representation as Strings.
     * <p>
     * dueDate and isCompleted can be represented with Strings. 
     * <p>
     * dueDate's format is specified by {@link DueDateConverter#PATTERN}
     * 
     * @see main.Task
     */
    public static class TaskUI extends Task {
        /**
         * Represents when a task is completed.
         */
        public static final String COMPLETED = "Completed";
        /**
         * Represent when a task is not completed.
         */
        public static final String NOT_COMPLETED = "No";

        private static final DueDateConverter converter = new DueDateConverter();

        /**
         * Same as {@code Task}.
         * 
         * @see main.Task#Task
         */
        public TaskUI(String title, String description, LocalDate dueDate, boolean isCompleted) {
            super(title, description, dueDate, isCompleted);
        }

        /**
         * Returns a {@code String} representing this task's due date.
         * <p>
         * Representation will follow: {@link DueDateConverter#PATTERN}.
         * 
         * @return {@code String} representing this task's due date.
         */
        public String getDueDateAsString() {
            LocalDate dueDate = super.getDueDate();

            if (dueDate == null) {
                return "";
            }
            return converter.toString(dueDate);
        } 

        /**
         * Returns a {@code String} representing whether this task is completed.
         * <p>
         * Representation will be: {@value #COMPLETED} or {@value #NOT_COMPLETED}
         * 
         * @return {@code String} representing whether this task is completed.
         */
        public String getIsCompletedAsString() {
            boolean isCompleted = super.getIsCompleted();

            if (isCompleted) {
                return COMPLETED;
            } else {
                return NOT_COMPLETED;
            }
        }
        
    }

    /**
     * {@code TaskUICellFactory} handles creation of each cell in the title, description, due date, 
     * and completed? columns.
     * <p>
     * Each cell will display it's data and double clicking a cell will trigger it's editing 
     * (though completed? columns will simply mark completed or not).
     */
    private static class TaskCellFactory implements 
            Callback<TableColumn<TaskUI, String>, TableCell<TaskUI, String>> {
        private Stage ownerStage;
        private ObservableList<TaskUI> data;
        
        /**
         * Initializes a newly created {@code TaskUICellFactory} object so it may create cells for
         * a table in the specified {@code Stage} with the specified data.
         * @param ownerStage {@code Stage} containing the table with the cells to create.
         * @param data table data for the table with the cells to create.
         */
        private TaskCellFactory(Stage ownerStage, ObservableList<TaskUI> data) {
            this.ownerStage = ownerStage;
            this.data = data;
        }

        /**
         * Returns a {@code TableCell<TaskUI, String>} that displays it's contents and handles
         * double click events. Double clicking opens edit windows for title, description, and 
         * due date columns; completed? column cells are marked completed or not. 
         * <p>
         * Cannot handle columns other than title, description, due date, and completed?
         * 
         * @param column The column this cell is a part of. Each column has a different way of 
         * handling double clicks.
         * @throws IllegalStateException if specified column is not handled or if a completed? 
         * column cell has unexpected text.
         */
        @Override
        public TableCell<TaskUI, String> call(TableColumn<TaskUI, String> column) {
            TableCell<TaskUI, String> cell = new TableCell<TaskUI, String>() {
                // "It is very important that subclasses of Cell override the updateItem method 
                // properly, as failure to do so will lead to issues such as blank cells or cells 
                // with unexpected content appearing within them."
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            // Handle double click events
            cell.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && !cell.isEmpty()) {
                    // Begin editing this cell
                    String columnName = column.getText();

                    if (columnName.equals(IS_COMPLETED_HEADER)) {
                        // Completed? column
                        String isCompleted = cell.getText();

                        // Determine opposite value
                        String opposite;
                        if (isCompleted.equals(TaskUI.COMPLETED)) {
                            opposite = TaskUI.NOT_COMPLETED;
                        } else if (isCompleted.equals(TaskUI.NOT_COMPLETED)) {
                            opposite = TaskUI.COMPLETED;
                        } else {
                            throw new IllegalStateException("Unknown state: " + isCompleted);
                        }

                        // Set cell to be the opposite value
                        data.get(cell.getIndex()).setIsCompleted(opposite == TaskUI.COMPLETED);
                        cell.setText(opposite);
                    } else {
                        // Prep the edit window
                        Stage inputStage = new Stage();
                        inputStage.setTitle("Edit " + columnName);
                        inputStage.initModality(Modality.APPLICATION_MODAL);
                        inputStage.initOwner(ownerStage);   

                        // edit window will have inputs and buttons
                        VBox inputVBox = new VBox(PADDING);
                        Button acceptButton = new Button("Accept");

                        if (columnName.equals(TITLE_HEADER)) {
                            // Edit title 
                            final Label label = new Label(TITLE_HEADER);
                            label.setFont(HEADER_FONT);

                            // Input for new title
                            final TextArea textArea = new TextArea();
                            textArea.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
                            textArea.setPrefHeight(0);

                            // Fill with current value and highlight
                            textArea.setText(cell.getText());
                            textArea.selectAll();

                            // Update data and table on press, ensuring non-empty
                            acceptButton.setOnAction((ActionEvent acceptEvent) -> {
                                String newTitle = textArea.getText();

                                if (newTitle.trim().isEmpty()) {
                                    // Must enter a non-empty title
                                    displayError(inputStage, TITLE_ERROR_MESSAGE);

                                    textArea.requestFocus();
                                } else {
                                    // Update
                                    data.get(cell.getIndex()).setTitle(newTitle);
                                    cell.setText(newTitle);

                                    inputStage.hide();
                                }
                            });

                            inputVBox.getChildren().addAll(label, textArea);
                        } else if (columnName.equals(DESCRIPTION_HEADER)) {
                            // Edit description
                            final Label label = new Label(DESCRIPTION_HEADER);
                            label.setFont(HEADER_FONT);

                            // Input for new description
                            final TextArea textArea = new TextArea();
                            textArea.setPrefWidth(DESCRIPTION_FIELD_WIDTH);

                            // Fill with current value and prep for editing
                            textArea.setText(cell.getText());
                            textArea.selectAll();

                            // Update data and table on press
                            acceptButton.setOnAction((ActionEvent acceptEvent) -> {
                                String newDescription = textArea.getText();

                                // Update
                                data.get(cell.getIndex()).setDescription(newDescription);
                                cell.setText(newDescription);

                                inputStage.hide();
                            });

                            inputVBox.getChildren().addAll(label, textArea);
                        } else if (columnName.equals(DUE_DATE_HEADER)) {
                            // Edit due date
                            final Label label = new Label(DUE_DATE_HEADER);
                            label.setFont(HEADER_FONT);

                            // Input for new due date
                            final DatePicker datePicker = new DatePicker();
                            DueDateConverter converter = new DueDateConverter();
                            datePicker.setConverter(converter);

                            // Load the cell value into the picker
                            datePicker.setValue(converter.fromString(cell.getText()));

                            // Update data and table on press
                            acceptButton.setOnAction((ActionEvent acceptEvent) -> {
                                if (converter.getStatus() == DueDateConverter.STATUS.SUCCESS) {
                                    LocalDate newDueDate = datePicker.getValue();

                                    // Update
                                    data.get(cell.getIndex()).setDueDate(newDueDate);
                                    cell.setText(converter.toString(newDueDate));

                                    inputStage.hide();
                                } else {
                                    displayError(inputStage, DUE_DATE_ERROR_MESSAGE);

                                    datePicker.requestFocus();
                                }
                            });

                            inputVBox.getChildren().addAll(label, datePicker);
                        } else {
                            throw new IllegalStateException("Unknown column: " + columnName);
                        }

                        // A way to back out for the user
                        Button cancelButton = new Button("Cancel");
                        cancelButton.setOnAction((ActionEvent cancelEvent) -> {
                            inputStage.hide();
                        });

                        // Button row, "Accept" next to "Cancel"
                        HBox buttonsHBox = new HBox(PADDING, acceptButton, cancelButton);

                        // Vertically align components
                        VBox vBox = new VBox(PADDING, inputVBox, buttonsHBox);
                        vBox.setPadding(new Insets(PADDING));

                        // Container enabling user interactions
                        Scene inputScene = new Scene(vBox);
                        inputStage.setScene(inputScene);

                        inputStage.show();
                    }
                }
            });

            return cell;
        }
    }

    /**
     * Handles conversion of {@code LocalDate} to and from {@code String}.
     * <p>
     * {@code String} must be in {@link DueDateConverter#PATTERN} format and toString will be
     * in that format.
     */
    private static class DueDateConverter extends StringConverter<LocalDate> {
        /**
         * Pattern each {@code LocalDate} as {@code String} must follow.
         * <p>
         * e.g. {@code 8/10/2023} for August 10th, 2023
         * 
         * @see DateTimeFormatter
         */
        public static final String PATTERN = "M/d/yyyy";

        private static final DateTimeFormatter FORMATTER = 
                DateTimeFormatter.ofPattern(PATTERN);

        /**
         * Represents the result of the most recent conversion.
         * <p>
         * Unknown when no conversions have occured.
         */
        public enum STATUS {
            SUCCESS,
            FAIL,
            UNKNOWN;
        }

        private STATUS status = STATUS.UNKNOWN;

        /**
         * Converts the provided {@code String} to {@code LocalDate} and returns it.
         * <p>
         * {@code DateTimeParseException} is not thrown and null is returned instead but
         * calls to {@code #getStatus()} return STATUS.FAIL.
         * 
         * @see DueDateConverter#PATTERN
         */
        @Override
        public LocalDate fromString(String formattedString) {
            if (formattedString.isBlank()) {
                // Exceptions are slow, handle common blank case.
                status = STATUS.SUCCESS; // No date selection allowed!
                return null;
            }

            // Attempt conversion, exceptions set status to STATUS.FAIL.
            try {
                LocalDate dueDate = LocalDate.from(FORMATTER.parse(formattedString));
                status = STATUS.SUCCESS;
                return dueDate;
            } catch (DateTimeParseException parseException) {
                status = STATUS.FAIL;
                return null;
            }
        }

        /**
         * Converts provided {@code LocalDate} into its {@code String} form. 
         * 
         * @see DueDateConverter#PATTERN
         */
        @Override
        public String toString(LocalDate dueDate) {
            if (dueDate == null) {
                return ""; // Handle common case of date unset.
            }
            return FORMATTER.format(dueDate);
        }

        public STATUS getStatus() {
            return status;
        }
    }
}