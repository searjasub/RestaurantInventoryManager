package controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainStageController {

    private Stage primaryStage;
    private Scene primaryScene;

    public MainStageController() {
    }

    public void setPrimaryStage(Stage stage, Scene scene){

        this.primaryStage = primaryStage;
        this.primaryScene = scene;

    }
}
