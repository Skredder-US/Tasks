package main;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
// import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
// import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
// import javafx.stage.Modality;
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
    private static final Font LABEL_FONT = new Font("Arial", 19);
    private static final double WINDOW_WIDTH = 1280;
    private static final double WINDOW_HEIGHT = 720;
    private static final double PADDING = 10;
    private static final String TITLE_HEADER = "Title";
    private static final String DESCRIPTION_HEADER = "Description";
    private static final String DUE_DATE_HEADER = "Due Date";
    private static final String IS_COMPLETED_HEADER = "Completed?";
    private static final int TITLE_COLUMN_MAX_WIDTH = 200;
    private static final int DUE_DATE_COLUMN_MAX_WIDTH = 165;
    // Description column fills the rest of the table width
    private static final int COMPLETE_MAX_WIDTH = 80;

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

        final Label label = new Label("Tasks");
        label.setFont(LABEL_FONT);
        // center label
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        final TableView<TaskUI> table = new TableView<TaskUI>();
        table.setMinWidth(WINDOW_WIDTH - 2 * PADDING);
        table.setMinHeight(WINDOW_HEIGHT - 80);
        // Columns fill the width of the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        List<TableColumn<TaskUI, String>> columns = createColumns();
        table.getColumns().addAll(columns);

        // dummy example data
        ObservableList<TaskUI> data = FXCollections.observableArrayList(
            new TaskUI("b", "b", "b", "no"),
            new TaskUI("a", "a", "a", "no"),
            new TaskUI("c", "c", "c", "completed")
        );
        table.setItems(data);

        // Button addButton = createAddButton(stage, data);

        final VBox vbox = new VBox();
        vbox.setSpacing(PADDING / 2);
        vbox.setPadding(new Insets(PADDING, 0, 0, PADDING));
        vbox.getChildren().addAll(label, table);
        
        Group root = new Group();
        root.getChildren().add(vbox);
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns a List of all the columns in the table of tasks. Columns are title, description, 
     * due date, and completed?.
     */ 
    private static List<TableColumn<TaskUI, String>> createColumns() {
        List<TableColumn<TaskUI, String>> columns = new ArrayList<TableColumn<TaskUI, String>>();

        TableColumn<TaskUI, String> titleColumn = new TableColumn<TaskUI, String>(TITLE_HEADER);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
        columns.add(titleColumn);

        TableColumn<TaskUI, String> descriptionColumn = 
                new TableColumn<TaskUI, String>(DESCRIPTION_HEADER);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        columns.add(descriptionColumn);

        TableColumn<TaskUI, String> dueDateColumn =
                new TableColumn<TaskUI, String>(DUE_DATE_HEADER);
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
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

        TableColumn<TaskUI, String> completeColumn =
                new TableColumn<TaskUI, String>(IS_COMPLETED_HEADER);
        completeColumn.setCellValueFactory(new PropertyValueFactory<>("isCompleted"));
        completeColumn.setMaxWidth(COMPLETE_MAX_WIDTH);
        columns.add(completeColumn);

        return columns;
    }

    // private static Button createAddButton(Stage mainStage, ObservableList<TaskUI> data) {
    //     final Button addButton = new Button("Add");

    //     addButton.setOnAction((ActionEvent addEvent) -> {
    //         final Stage stage = new Stage();
    //         stage.setTitle("Create Task");
    //         // blocks events from being delivered to any other app window
    //         stage.initModality(Modality.APPLICATION_MODAL);
    //         stage.initOwner(mainStage);

    //         final Label titleLabel = new Label(TITLE_HEADER);
    //         titleLabel.setFont(LABEL_FONT);
    //         final TextArea titleField = new TextArea();
    //         titleField.setMaxWidth(TITLE_COLUMN_MAX_WIDTH);
    //         titleField.setPrefHeight(0);

    //         final Label descriptionLabel = new Label(DESCRIPTION_HEADER);
    //         descriptionLabel.setFont(LABEL_FONT);
    //         final TextArea descriptionField = new TextArea();
    //         descriptionField.setMaxWidth(800);

    //         final Label dueDateLabel = new Label(DUE_DATE_HEADER);
    //         dueDateLabel.setFont(LABEL_FONT);
    //         final DateTimePicker dateTimePicker = new DateTimePicker();

    //         final Label isCompletedLabel = new Label(IS_COMPLETED_HEADER);
    //         isCompletedLabel.setFont(LABEL_FONT);
    //         final CheckBox checkBox = new CheckBox();

    //         final Button createButton = new Button("Create");
    //         createButton.setOnAction((ActionEvent createEvent) -> {
    //             data.add(new TaskUI(titleField.getText(), descriptionField.getText(),
    //                     dateTimePicker.getDateTimeValue().toString(),
    //                     checkBox.selectedProperty().toString()));
    //             stage.hide();
    //         });

    //         VBox vbox = new VBox(20);
    //         vbox.setSpacing(PADDING / 2);
    //         vbox.setPadding(new Insets(PADDING, PADDING, 0, PADDING));
    //         vbox.getChildren().addAll(titleLabel, titleField, descriptionLabel, descriptionField,
    //                 dueDateLabel, dateTimePicker, isCompletedLabel, checkBox, createButton);

    //         Scene dialogScene = new Scene(vbox, 810, 455);
    //         stage.setScene(dialogScene);
    //         stage.show();
    //     });

    //     return addButton;
    // }

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
