package controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.Employee;

import java.io.IOException;

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

        //Root employee (Manager)
        Employee manager = new Employee();
        manager.setName("Admin");
        manager.setPassword("password");
        manager.setId(30001);
        //Simple employee (Worker)
        Employee employee1 = new Employee();
        employee1.setName("Jared");
        employee1.setPassword("password");
        employee1.setId(12345);


    }

    public void onMenuItemExit(ActionEvent actionEvent) {
        primaryStage.close();
    }

    public void login(ActionEvent actionEvent) throws IOException {
        int isManager = Integer.parseInt(usernameTextField.getText(0,1));
        if (isManager == 3){
            Scene tmp = this.scene;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdministrativeScene.fxml"));
            BorderPane root = loader.load();
            AdministrativeController adminController = loader.getController();
            Scene administrativeScene = new Scene(root, 600, 600);

            adminController.setPrimaryScene(primaryStage, tmp, this);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(administrativeScene);

        } else {


        }

    }
}
