package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Employee;

import java.io.IOException;
import java.util.HashMap;

public class MainStageController {

    public TextField usernameTextField;
    public TextField passwordTextField;
    public Button loginBtn;
    private Stage primaryStage;
    private Scene scene;
    private HashMap<Integer, Employee> employeeCollection = new HashMap<>();

    public MainStageController() {

    }

    public void setPrimaryStage(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.scene = scene;

               //Root employee (Manager)
        Employee manager = new Employee();
        manager.setName("Admin");
        manager.setPassword("password2");
        manager.setId("30001");
        //Simple employee (Worker)
        Employee employee1 = new Employee();
        employee1.setName("Jared");
        employee1.setPassword("password1");
        employee1.setId("12345");

        employeeCollection.put(30001, manager);
        employeeCollection.put(12345, employee1);


    }

    public void onMenuItemExit() {
        primaryStage.close();
    }

    public void login() throws IOException {
        Scene tmp = this.scene;
        int employeeIdentifier = Integer.parseInt(usernameTextField.getText(0, 1));
        boolean isManager;

        if (employeeIdentifier == 3){
            isManager = true;
        } else {
            isManager = false;
        }

        if (isManager) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdministrativeScene.fxml"));
            BorderPane root = loader.load();
            AdministrativeController adminController = loader.getController();
            Scene administrativeScene = new Scene(root, 600, 600);

            adminController.setPrimaryStage(primaryStage, tmp, this, employeeCollection, true);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(administrativeScene);

        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../POSScene.fxml"));
            BorderPane root = loader.load();
            POSController posController = loader.getController();
            Scene posScene = new Scene(root, 600, 600);

            posController.setPrimaryStage(primaryStage, tmp, this, employeeCollection, false);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(posScene);

        }

    }
}
