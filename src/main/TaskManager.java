package main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
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
 * A simple task management application that allows users to add, update, and track tasks.  
 * <p>
 * {@code TaskManager} includes a sortable task list displaying all tasks with their details 
 * (title, description, due date, and whether it's completed). Click the column header to sort
 * alphabetically, click again for more options. Shift clicking sets secondary sorting, e.g. 
 * clicking "Is Completed?" then shift clicking "Due Date" will sort by completion then date.
 * <p>
 * Tasks can be created, edited, and deleted. Double click a cell to edit it! Create and delete
 * are buttons. Select a row then press delete to delete that task.
 */
public class TaskManager extends Application {
    private static final String SAVE_FILENAME = "Tasks.ser";
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

    private ObservableList<TaskUI> tasks;

    /**
     * Launches the application, causing the creation and showing of the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates and shows the application, causing it to appear on the screen to be used.
     * 
     * @param primaryStage the primary stage for this application. See super for more info.
     */ 
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Manager");

        // Label the table, tasks are here
        Label label = new Label("Tasks");
        label.setFont(HEADER_FONT);
        // center label
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        // build the main table
        TableView<TaskUI> table = new TableView<TaskUI>();
        table.setPrefWidth(TABLE_WIDTH);
        table.setPrefHeight(TABLE_HEIGHT);
        // Columns fill the width of the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // load saved tasks
        loadSavedTasks(); // loads to tasks field
        table.setItems(tasks);

        // Add the table columns
        List<TableColumn<TaskUI, String>> columns = createColumns(primaryStage);
        table.getColumns().addAll(columns);

        // Add button for Task creation
        Button addButton = createAddButton(primaryStage);

        // Add button for Task deletion
        Button deleteButton = createDeleteButton(table);

        // Button row, "Accept" next to "Cancel"
        HBox buttonsHBox = new HBox(PADDING, addButton, deleteButton);

        // Vertically stack the elements
        VBox vBox = new VBox(PADDING, label, table, buttonsHBox);
        vBox.setPadding(new Insets(PADDING));
        
        // Container enabling user interactions
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);

        // display window
        primaryStage.show();
    }

    /**
     * Saves current state of tasks to a serialization file.
     */
    @Override
    public void stop() {
        // Serialization
        try {  
            FileOutputStream file = new FileOutputStream(SAVE_FILENAME);
            ObjectOutputStream out = new ObjectOutputStream(file);
             
            out.writeObject(new ArrayList<TaskUI>(tasks));
             
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes and populates the list of tasks from the save file. No save file results
     * in an empty list!
     */
    @SuppressWarnings("unchecked")
    private void loadSavedTasks() {
        tasks = FXCollections.observableArrayList();

        try {
            InputStream in = Files.newInputStream(Path.of(SAVE_FILENAME));
            ObjectInputStream ois = new ObjectInputStream(in);

            List<TaskUI> list = (List<TaskUI>) ois.readObject();

            tasks.addAll(list);
        } catch (NoSuchFileException e) {
            // continue without loading tasks because there were none to load.
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a List of all the columns in the table of tasks. Columns are title, description, 
     * due date, and completed?. Columns can be edited by double clicking. Due date column sorts
     * chronologically while the others sort alphabetically.
     */ 
    private List<TableColumn<TaskUI, String>> createColumns(Stage ownerStage) {
        List<TableColumn<TaskUI, String>> columns = new ArrayList<TableColumn<TaskUI, String>>();

        // Title
        TableColumn<TaskUI, String> titleColumn = 
                new TableColumn<TaskUI, String>(TITLE_HEADER);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(new TaskCellFactory(ownerStage));
        titleColumn.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
        columns.add(titleColumn);

        // Description
        TableColumn<TaskUI, String> descriptionColumn = 
                new TableColumn<TaskUI, String>(DESCRIPTION_HEADER);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(new TaskCellFactory(ownerStage));
        // width = as big as it can be
        columns.add(descriptionColumn);

        // Due Date
        TableColumn<TaskUI, String> dueDateColumn =
                new TableColumn<TaskUI, String>(DUE_DATE_HEADER);
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDateAsString"));
        dueDateColumn.setCellFactory(new TaskCellFactory(ownerStage));
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
        completeColumn.setCellFactory(new TaskCellFactory(ownerStage));
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
     * @return The add button
     */
    private Button createAddButton(Stage ownerStage) {
        // Create an input submition button 
        Button addButton = new Button("Add");

        addButton.setOnAction((ActionEvent addEvent) -> {
            // Prep the window
            Stage stage = new Stage();
            stage.setTitle("Create Task");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerStage);

            // title input
            Label titleLabel = new Label(TITLE_HEADER);
            titleLabel.setFont(HEADER_FONT);
            TextArea titleField = new TextArea();
            titleField.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
            titleField.setPrefHeight(0);

            // description input
            Label descriptionLabel = new Label(DESCRIPTION_HEADER);
            descriptionLabel.setFont(HEADER_FONT);
            TextArea descriptionField = new TextArea();
            descriptionField.setPrefWidth(DESCRIPTION_FIELD_WIDTH);
            
            // due date input
            Label dueDateLabel = new Label(DUE_DATE_HEADER);
            dueDateLabel.setFont(HEADER_FONT);
            DatePicker datePicker = new DatePicker();
            DueDateConverter converter = new DueDateConverter();
            datePicker.setConverter(converter);

            // completed? input
            Label isCompletedLabel = new Label(IS_COMPLETED_HEADER);
            isCompletedLabel.setFont(HEADER_FONT);
            CheckBox isCompletedCheckBox = new CheckBox();

            // create a TaskUI on button press, prompt for title when empty
            Button createButton = new Button("Create");
            createButton.setOnAction((ActionEvent createEvent) -> {
                String title = titleField.getText();

                if (title.trim().isEmpty()) {
                    displayError(stage, TITLE_ERROR_MESSAGE);

                    titleField.requestFocus();
                } else if (converter.parseFailed) {
                    displayError(stage, DUE_DATE_ERROR_MESSAGE);
                    
                    datePicker.requestFocus();
                    datePicker.getEditor().selectAll();
                } else {
                    String description = descriptionField.getText();
                    LocalDate dueDate = datePicker.getValue();
                    boolean isCompleted = isCompletedCheckBox.isSelected();
                    
                    tasks.add(new TaskUI(title, description, dueDate, isCompleted));

                    stage.hide();
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

    private static Button createDeleteButton(TableView<TaskUI> table) {
        Button button = new Button("Delete");

        button.setOnAction((ActionEvent event) -> {
            int i = table.getSelectionModel().getSelectedIndex();

            if (i != -1) {
                table.getItems().remove(i);
            }
        });

        return button;
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
        Label label = new Label(text);
        label.setFont(HEADER_FONT);
        label.setTextAlignment(TextAlignment.CENTER);

        // Button to exit
        Button button = new Button("Okay");
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
     * {@code TaskCellFactory} handles creation of each cell in the title, description, due date, 
     * and completed? columns.
     * <p>
     * Each cell will display it's data and double clicking a cell will trigger it's editing 
     * (though completed? columns will simply mark completed or not).
     */
    private class TaskCellFactory implements 
            Callback<TableColumn<TaskUI, String>, TableCell<TaskUI, String>> {
        private Stage ownerStage;
        
        /**
         * Initializes a newly created {@code TaskCellFactory} object so it may create cells for
         * a table in the specified {@code Stage}.
         * @param ownerStage {@code Stage} containing the table with the cells to create.
         */
        private TaskCellFactory(Stage ownerStage) {
            this.ownerStage = ownerStage;
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
                        tasks.get(
                                cell.getIndex()).setIsCompleted(opposite.equals(TaskUI.COMPLETED));
                        cell.setText(opposite);
                    } else {
                        // Prep the edit window
                        Stage stage = new Stage();
                        stage.setTitle("Edit " + columnName);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initOwner(ownerStage);   

                        // edit window will have inputs and buttons
                        VBox inputVBox = new VBox(PADDING);
                        Label label = new Label();
                        label.setFont(HEADER_FONT);
                        Button acceptButton = new Button("Accept");

                        if (columnName.equals(TITLE_HEADER)) {
                            // Edit title 
                            label.setText(TITLE_HEADER);

                            // Input for new title
                            TextArea textArea = new TextArea();
                            textArea.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
                            textArea.setPrefHeight(0);

                            // Fill with current value and highlight
                            textArea.setText(cell.getText());
                            textArea.selectAll();

                            // Update tasks and cell on press, ensuring non-empty
                            acceptButton.setOnAction((ActionEvent acceptEvent) -> {
                                String newTitle = textArea.getText();

                                if (newTitle.isBlank()) {
                                    // Must enter a non-empty title
                                    displayError(stage, TITLE_ERROR_MESSAGE);

                                    textArea.requestFocus();
                                    textArea.selectAll();
                                } else {
                                    // Update
                                    tasks.get(cell.getIndex()).setTitle(newTitle);
                                    cell.setText(newTitle);

                                    stage.hide();
                                }
                            });

                            inputVBox.getChildren().addAll(label, textArea);
                        } else if (columnName.equals(DESCRIPTION_HEADER)) {
                            // Edit description
                            label.setText(DESCRIPTION_HEADER);

                            // Input for new description
                            TextArea textArea = new TextArea();
                            textArea.setPrefWidth(DESCRIPTION_FIELD_WIDTH);

                            // Fill with current value and prep for editing
                            textArea.setText(cell.getText());
                            textArea.selectAll();

                            // Update tasks and table on press
                            acceptButton.setOnAction((ActionEvent acceptEvent) -> {
                                String newDescription = textArea.getText();

                                // Update
                                tasks.get(cell.getIndex()).setDescription(newDescription);
                                cell.setText(newDescription);

                                stage.hide();
                            });

                            inputVBox.getChildren().addAll(label, textArea);
                        } else if (columnName.equals(DUE_DATE_HEADER)) {
                            // Edit due date
                            label.setText(DUE_DATE_HEADER);

                            // Input for new due date
                            DatePicker datePicker = new DatePicker();
                            DueDateConverter converter = new DueDateConverter();
                            datePicker.setConverter(converter);

                            // Load the cell value into the picker
                            datePicker.setValue(converter.fromString(cell.getText()));

                            // Update tasks and table on press
                            acceptButton.setOnAction((ActionEvent acceptEvent) -> {
                                if (converter.parseFailed) {
                                    displayError(stage, DUE_DATE_ERROR_MESSAGE);

                                    datePicker.requestFocus();
                                } else {
                                    LocalDate newDueDate = datePicker.getValue();

                                    // Update
                                    tasks.get(cell.getIndex()).setDueDate(newDueDate);
                                    cell.setText(converter.toString(newDueDate));

                                    stage.hide();
                                }
                            });

                            inputVBox.getChildren().addAll(label, datePicker);
                        } else {
                            throw new IllegalStateException("Unknown column: " + columnName);
                        }

                        // A way to back out for the user
                        Button cancelButton = new Button("Cancel");
                        cancelButton.setOnAction((ActionEvent cancelEvent) -> {
                            stage.hide();
                        });

                        // Button row, "Accept" next to "Cancel"
                        HBox buttonsHBox = new HBox(PADDING, acceptButton, cancelButton);

                        // Vertically align components
                        VBox vBox = new VBox(PADDING, inputVBox, buttonsHBox);
                        vBox.setPadding(new Insets(PADDING));

                        // Container enabling user interactions
                        Scene inputScene = new Scene(vBox);
                        stage.setScene(inputScene);

                        stage.show();
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
         * false when no conversions have occured.
         */
        public boolean parseFailed = false;

        /**
         * Converts the provided {@code String} to {@code LocalDate} and returns it.
         * <p>
         * {@code DateTimeParseException} is not thrown but null is returned instead and
         * parseFailed is set to true.
         * 
         * @see DueDateConverter#PATTERN
         */
        @Override
        public LocalDate fromString(String formattedString) {
            if (formattedString.isBlank()) {
                // Exceptions are slow, handle common blank case.
                parseFailed = false; // No date selection allowed!
                return null;
            }

            // Attempt conversion, exceptions set status to STATUS.FAIL.
            try {
                LocalDate dueDate = LocalDate.from(FORMATTER.parse(formattedString));
                parseFailed = false;
                return dueDate;
            } catch (DateTimeParseException parseException) {
                parseFailed = true;
                return null;
            }
        }

        /**
         * Converts provided {@code LocalDate} into its {@code String} form. 
         * <p>
         * null (or no) due dates return an empty {@code String}.
         * @see DueDateConverter#PATTERN
         */
        @Override
        public String toString(LocalDate dueDate) {
            if (dueDate == null) {
                return ""; // Handle common case of date unset.
            }
            return FORMATTER.format(dueDate);
        }
    }
}