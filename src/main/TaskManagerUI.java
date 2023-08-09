package main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * A simple graphical user interface (GUI) to interact with {@code TaskManager}. 
 * <p>
 * {@code TaskManagerUI} includes a sortable task list displaying all tasks with their details 
 * (title, description, due date, and whether it's completed). Click the column header to sort
 * alphabetically, click again for more options. Shift clicking sets secondary sorting, e.g. 
 * clicking "Is Completed?" then shift clicking "Due Date" will sort by completion then date.
 */
public class TaskManagerUI extends Application {
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
    private static final String COMPLETED = "completed";
    private static final String NOT_COMPLETED = "no";
    private static final int DATE_LENGTH = LocalDate.now().toString().length();

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

        // Label the table, tasks are here
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
            new TaskUI("b", "b", "", NOT_COMPLETED),
            new TaskUI("a", "a", "", NOT_COMPLETED),
            new TaskUI("c", "c", "", COMPLETED)
        );
        table.setItems(data);

        // Add the table columns
        List<TableColumn<TaskUI, String>> columns = createColumns(stage, data);
        table.getColumns().addAll(columns);

        // Add button for task creation
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
     * Returns a List of all the columns in the table of tasks. Columns are title, description, 
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
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateColumn.setCellFactory(new TaskCellFactory(ownerStage, data));
        // Sort chronologically
        dueDateColumn.setComparator(new Comparator<String>() {
            @Override
            public int compare(String dateTime1, String dateTime2) {
                // Techincally sorts alphabetically but coverting to 24 hour time makes 
                // it the same order as chronologically!
                dateTime1 = convert12To24Hour(dateTime1);
                dateTime2 = convert12To24Hour(dateTime2);
                return dateTime1.compareTo(dateTime2);
            }
        });
        dueDateColumn.setMaxWidth(DUE_DATE_COLUMN_MAX_WIDTH);
        columns.add(dueDateColumn);

        // Complete? column
        TableColumn<TaskUI, String> completeColumn =
                new TableColumn<TaskUI, String>(IS_COMPLETED_HEADER);
        completeColumn.setCellValueFactory(new PropertyValueFactory<>("isCompleted"));
        completeColumn.setCellFactory(new TaskCellFactory(ownerStage, data));
        completeColumn.setMaxWidth(COMPLETE_MAX_WIDTH);
        columns.add(completeColumn);

        return columns;
    }

    /**
     * Converts and returns specified date and time {@code String} from 12 hour to 24 hour time.
     * <p>
     * Assumptions:
     * <ul>
     *   <li>Specified time is 12 hour in the form: "hour:minutes AM/PM"</li>
     * (e.g. 12:00 AM and 1:00 PM). 
     *   <li>Specified date is in same form as {@code LocalDate#toString()}</li>
     * @param dateTime {@code String} containing a date and 12 hour time in specified formats.
     * @return {@code String} of specified date and time but now with 24 hour time.
     * @see java.time.LocalDate
     */
    // // Test for converting 12 to 24 hours
    // String date = "2023-08-08";
    // String[] amPm = {"AM", "PM"};
    // for (int i = 0; i < 2; i++) {
    //     for (int j = 1; j <= 12; j++) {
    //         String dateTime = date + " " + j + ":00 " + amPm[i];
    //         dateTime = convert12To24Hour(dateTime);
    //         System.out.println(dateTime);
    //     }
    // }
    private static String convert12To24Hour(String dateTime) {
        int colonIndex = dateTime.indexOf(":");

        if (colonIndex != -1) {
            // has time
            String hour12 = dateTime.substring(DATE_LENGTH, colonIndex).trim();
            int hour = Integer.parseInt(hour12);
            
            // convert to 24 hour
            if (hour == 12 && dateTime.contains("AM")) {
                hour = 0;
            } else if (hour != 12 && dateTime.contains("PM")) {
                hour += 12;
            }

            // add leading zeros
            String hour24 = Integer.toString(hour);
            if (hour < 10) {
                hour24 = "0" + hour24;
            }
            
            // convert
            dateTime = dateTime.substring(0, DATE_LENGTH) + // keep date the same
                    dateTime.substring(DATE_LENGTH, dateTime.length() - 3) // remove AM/PM
                    .replace(hour12, hour24); // don't change numbers in date
        }

        return dateTime;
    }

    /**
     * Creates an add button triggering a Task creation window upon press. 
     * <p>
     * The Task creation window has inputs for title, description, due date, and completed?. 
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
            final DateTimePicker dueDatePicker = new DateTimePicker(PADDING);

            // completed? input
            final Label isCompletedLabel = new Label(IS_COMPLETED_HEADER);
            isCompletedLabel.setFont(HEADER_FONT);
            final CheckBox isCompletedCheckBox = new CheckBox();

            // create a Task on button press, prompt for title when empty
            final Button createButton = new Button("Create");
            createButton.setOnAction((ActionEvent createEvent) -> {
                String title = titleField.getText();

                if (title.trim().isEmpty()) {
                    promptForTitle(stage);
                    titleField.requestFocus();
                    titleField.setText("");
                } else {
                    String description = descriptionField.getText();
                    String dueDate = dueDatePicker.toString();

                    String isCompleted;
                    if (isCompletedCheckBox.isSelected()) {
                        isCompleted = COMPLETED;
                    } else {
                        isCompleted = NOT_COMPLETED;
                    }
                    
                    data.add(new TaskUI(title, description, dueDate, isCompleted));
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
                    descriptionField, dueDateLabel, dueDatePicker, isCompletedLabel,
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
     * A small window requesting the user enter the title of the Task. 
     * @param ownerStage {@code Stage} from where this window was triggered.
     */
    private static void promptForTitle(Stage ownerStage) {
        // Prep window
        Stage alertStage = new Stage();
        alertStage.setTitle("Invalid");
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.initOwner(ownerStage);

        // Label explaining to user
        final Label alertLabel = new Label("Please enter a title.");
        alertLabel.setFont(HEADER_FONT);

        // Button to exit
        final Button okayButton = new Button("Okay");
        okayButton.setOnAction((ActionEvent createEvent) -> {
            alertStage.hide();
        });

        // Vertically stack the components and center
        VBox vBox = new VBox(30, alertLabel, okayButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(40, 0, 0, 0));

        // Container enabling user interactions
        Scene dialogScene = new Scene(vBox, 220, 150);
        alertStage.setScene(dialogScene);
        
        // display window
        alertStage.show();
    }

    /**
     * Stores and provides accessors for the data of a Task. Task data is made up of title, 
     * description, due date, and whether it's completed. 
     * <p>
     * {@code TaskUI} is used for displaying in UIs, unlike the {@code Task} class.
     * 
     * @see main.Task
     */
    // Null checking will be done by Task after integrating. 
    public static class TaskUI {
        private final SimpleStringProperty title;
        private final SimpleStringProperty description;
        private final SimpleStringProperty dueDate;
        private final SimpleStringProperty isCompleted;
 
        /**
         * Initializes a newly created {@code TaskUI} object so it represents the task with the 
         * specified information.
         * 
         * @param title title of the task.
         * @param description description of the task.
         * @param dueDate when the task is due.
         * @param isCompleted whether the task is completed.
         */
        private TaskUI(String title, String description, String dueDate, String isCompleted) {
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty(description);
            this.dueDate = new SimpleStringProperty(dueDate);
            this.isCompleted = new SimpleStringProperty(isCompleted);
        }

        /**
         * Gets the title of this task.
         * @return A String representing the title of this task.
         */
        public String getTitle() {
            return title.get();
        }

        /**
         * Gets the description of this task.
         * @return A String representing the description of this task.
         */
        public String getDescription() {
            return description.get();
        }

        /**
         * Gets the due date of this task as display in the UI.
         * @return A String representing the due date of this task in the form "yyyy-mm-dd hh:mm".
         */
        public String getDueDate() {
            return dueDate.get();
        }

        /**
         * Gets whether this task is completed as display in the UI.
         * @return A String representing whether this task is completed.
         */
        public String getIsCompleted() {
            return isCompleted.get();
        }
        /**
         * Sets the title of this task.
         * @param title The value used to set the title.
         */
        public void setTitle(String title) {
            this.title.set(title);
        }
        
        /**
         * Sets the description of this task.
         * @param description The value used to set the description.
         */
        public void setDescription(String description) {
            this.description.set(description);
        }
        
        /**
         * Sets the due date of this task.
         * @param title The String used to set the due date in the form "yyyy-mm-dd hh:mm".
         */
        public void setDueDate(String dueDate) {
            this.dueDate.set(dueDate);
        }

        /**
         * Sets whether this task is completed.
         * @param isCompleted the value used to set whether this task is completed.
         */
        public void setIsCompleted(String isCompleted) {
            this.isCompleted.set(isCompleted);
        }
    }

    /**
     * {@code TaskCellFactory} handles creation of each cell in the title, description, due date, 
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
         * Initializes a newly created {@code TaskCellFactory} object so it may create cells for
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
                        if (isCompleted.equals(COMPLETED)) {
                            opposite = NOT_COMPLETED;
                        } else if (isCompleted.equals(NOT_COMPLETED)) {
                            opposite = COMPLETED;
                        } else {
                            throw new IllegalStateException("Unknown state: " + isCompleted);
                        }

                        // Set cell to be the opposite value
                        data.get(cell.getIndex()).setIsCompleted(opposite);
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
                                    promptForTitle(inputStage);

                                    textArea.requestFocus();
                                    textArea.setText("");
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
                            final DateTimePicker dueDatePicker = new DateTimePicker(PADDING);

                            // Fill with current value and prep for editing
                            String dateTime = cell.getText();
                            if (!dateTime.isEmpty()) {
                                // has date
                                dueDatePicker.setDate(dateTime.substring(0, DATE_LENGTH));

                                if (dateTime.length() > DATE_LENGTH) {
                                    // has time
                                    dueDatePicker.setTime(dateTime.substring(
                                            DATE_LENGTH, dateTime.length()).trim());
                                }
                            }

                            // Update data and table on press
                            acceptButton.setOnAction((ActionEvent acceptEvent) -> {
                                String newDueDate = dueDatePicker.toString();

                                // Update
                                data.get(cell.getIndex()).setDueDate(newDueDate);
                                cell.setText(newDueDate);

                                inputStage.hide();
                            });

                            inputVBox.getChildren().addAll(label, dueDatePicker);
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
}
