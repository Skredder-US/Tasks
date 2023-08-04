package main;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TaskManagerUI extends Application {
    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1080, 1080);
        stage.setScene(scene);

        stage.setTitle("Task Manager");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
