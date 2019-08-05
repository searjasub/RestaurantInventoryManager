package controllers;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Employee;

public class MainStageController {

    public TextField usernameTextField;
    public TextField passwordTextField;
    public Button loginBtn;
    private Stage primaryStage;
    private Scene scene;

    public MainStageController() {

    }

    public void setPrimaryStage(Stage primaryStage, Scene scene){
        this.primaryStage = primaryStage;
        this.scene = scene;

        loginScreen();

    }

    private void loginScreen() {
        Employee manager = new Employee();
        manager.setName("Admin");
        manager.setPassword("password");
        manager.setId(30001);




    }

    public void onMenuItemExit(ActionEvent actionEvent) {
        primaryStage.close();
    }
}
