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
// import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final int PADDING = 10;
    private static final String TITLE_HEADER = "Title";
    private static final String DESCRIPTION_HEADER = "Description";
    private static final String DUE_DATE_HEADER = "Due Date";
    private static final String IS_COMPLETED_HEADER = "Completed?";
    private static final int TITLE_COLUMN_MAX_WIDTH = 200;
    private static final int DUE_DATE_COLUMN_MAX_WIDTH = 165;
    private static final int COMPLETE_MAX_WIDTH = 80;
    private static final int DESCRIPTION_FIELD_WIDTH = WINDOW_WIDTH - PADDING
            - TITLE_COLUMN_MAX_WIDTH - DUE_DATE_COLUMN_MAX_WIDTH - COMPLETE_MAX_WIDTH;
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
        table.setMinWidth(WINDOW_WIDTH - 2 * PADDING);
        table.setMinHeight(WINDOW_HEIGHT - 80);
        // Columns fill the width of the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Add the columns
        List<TableColumn<TaskUI, String>> columns = createColumns();
        table.getColumns().addAll(columns);

        // dummy example data
        ObservableList<TaskUI> data = FXCollections.observableArrayList(
            new TaskUI("b", "b", "b", "no"),
            new TaskUI("a", "a", "a", "no"),
            new TaskUI("c", "c", "c", "completed")
        );
        table.setItems(data);

        // Add button for task creation
        Button addButton = createAddButton(stage, data);

        // Vertically stack the elements
        final VBox vBox = new VBox(PADDING / 2, label, table, addButton);
        vBox.setPadding(new Insets(PADDING, PADDING, 0, PADDING));
        
        // Create event target for user interaction
        Scene scene = new Scene(vBox, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);

        // dispay window
        stage.show();
    }

    /**
     * Returns a List of all the columns in the table of tasks. Columns are title, description, 
     * due date, and completed?.
     */ 
    private static List<TableColumn<TaskUI, String>> createColumns() {
        List<TableColumn<TaskUI, String>> columns = new ArrayList<TableColumn<TaskUI, String>>();

        // Title
        TableColumn<TaskUI, String> titleColumn = new TableColumn<TaskUI, String>(TITLE_HEADER);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
        columns.add(titleColumn);

        // Description
        TableColumn<TaskUI, String> descriptionColumn = 
                new TableColumn<TaskUI, String>(DESCRIPTION_HEADER);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        columns.add(descriptionColumn);

        // Due Date
        TableColumn<TaskUI, String> dueDateColumn =
                new TableColumn<TaskUI, String>(DUE_DATE_HEADER);
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        // Sort chronologically
        dueDateColumn.setComparator(new Comparator<String>() {
            @Override
            public int compare(String dateTime1, String dateTime2) {
                // Test for converting 12 to 24 hours
                // String date = "2023-08-08";
                // for (int i = 1; i <= 12; i++) {
                //     String dateTime = date + " " + i + ":00 AM";
                //     dateTime = convert12To24Hour(dateTime);
                //     System.out.println(dateTime);
                // }
                // for (int i = 1; i <= 12; i++) {
                //     String dateTime = date + " " + i + ":00 PM";
                //     dateTime = convert12To24Hour(dateTime);
                //     System.out.println(dateTime);
                // }
                
                // Techincally sorts alphabetically but coverting to 24 hour time makes 
                // it the same order as chronologically!
                dateTime1 = convert12To24Hour(dateTime1);
                dateTime2 = convert12To24Hour(dateTime2);
                return dateTime1.compareTo(dateTime2);
            }
        });
        // dueDateColumn.setCellFactory(column -> {
        //     TableCell<TaskUI, String> cell = new TableCell<TaskUI, String>() {
        //         @Override
        //         protected void updateItem(String item, boolean empty) {
        //             super.updateItem(item, empty) ;
        //             setText(empty ? null : item);
        //         }
        //     };

        //     cell.setOnMouseClicked(mouseEvent -> {
        //         if (mouseEvent.getClickCount() == 2 && !cell.isEmpty()) {
        //             String userId = cell.getItem();
        //             System.out.println(userId);
        //         }
        //     });
        //     return cell ;
        // });
        dueDateColumn.setMaxWidth(DUE_DATE_COLUMN_MAX_WIDTH);
        columns.add(dueDateColumn);

        // Complete?
        TableColumn<TaskUI, String> completeColumn =
                new TableColumn<TaskUI, String>(IS_COMPLETED_HEADER);
        completeColumn.setCellValueFactory(new PropertyValueFactory<>("isCompleted"));
        completeColumn.setMaxWidth(COMPLETE_MAX_WIDTH);
        columns.add(completeColumn);

        return columns;
    }
    
    /**
     * Converts and returns specified date and time {@code String} from 12 hour to 24 hour time.
     * <p>
     * Assumptions:
     * <ul>
     *   <li>Specified time is 12 hour in the form: "hour:minutes AM/PM" 
     * (e.g. 12:00 AM and 1:00 PM). 
     *   <li>Specified date is in same form as {@code LocalDate#toString()}
     * @param dateTime {@code String} containing a date and 12 hour time in specified formats.
     * @return {@code String} of specified date and time but now with 24 hour time.
     * @see java.time.LocalDate
     */
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
            descriptionField.setMaxWidth(DESCRIPTION_FIELD_WIDTH);
            
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
                        isCompleted = "completed";
                    } else {
                        isCompleted = "no";
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
            vBox.setPadding(new Insets(0, PADDING, 0, PADDING));

            // Create event target for user interactions
            int width = DESCRIPTION_FIELD_WIDTH + PADDING;
            Scene dialogScene = new Scene(vBox, width, width / 16 * 9);
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

        // Create event target for user interaction
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
         * @return A String representing whether this task is completed: either "completed" or "no".
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
}
