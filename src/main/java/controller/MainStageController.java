package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Employee;
import org.bson.Document;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainStageController {

    public TextField usernameTextField;
    public TextField passwordTextField;
    public Button loginBtn;
    public TextField passwordField;
    public CheckBox checkbox;
    private Stage primaryStage;
    private Scene scene;
    private HashMap<Integer, Employee> employeeCollection = new HashMap<>();
    private HashMap<Integer, Employee> adminMap = new HashMap<>();
    private CurrentSession currentSession = new CurrentSession();
    private MongoController mCon = new MongoController();


    public MainStageController() throws UnknownHostException {

    }

    public void setPrimaryStage(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.scene = scene;
        employeeCollection = mCon.fillEmpCollection();
        adminMap = mCon.fillAdminCollection();

        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);

        passwordField.managedProperty().bind(checkbox.selectedProperty());
        passwordField.visibleProperty().bind(checkbox.selectedProperty());

        passwordTextField.managedProperty().bind(checkbox.selectedProperty().not());
        passwordTextField.visibleProperty().bind(checkbox.selectedProperty().not());

        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());


    }

    public void onMenuItemExit() {
        primaryStage.close();
    }

    public void login() throws IOException {
        Scene tmp = this.scene;
        int employeeIdentifier = Integer.parseInt(usernameTextField.getText(0, 1));

        //Simplified if else statement
        boolean isManager = employeeIdentifier == 3;

        if (isManager) {
            Employee e = adminMap.get(Integer.parseInt(usernameTextField.getText()));
            currentSession.setLoggedIn(e);
            if (e.equals(null)) {
                //Dialog telling user username is not valid
            } else if (!e.equals(null)) {
                if (passwordTextField.getText().equals(e.getPassword())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdministrativeScene.fxml"));
                    BorderPane root = loader.load();
                    AdministrativeController adminController = loader.getController();
                    Scene administrativeScene = new Scene(root, 600, 600);

                    adminController.setPrimaryStage(primaryStage, tmp, this, employeeCollection, currentSession);
                    primaryStage.setMaxWidth(600);
                    primaryStage.setMaxHeight(600);
                    primaryStage.setScene(administrativeScene);
                } else {
                    //Dialog telling user password is incorrect
                }
            }
        } else {
            Employee e = employeeCollection.get(Integer.parseInt(usernameTextField.getText()));
            if (e.equals(null)) {
                //Dialog telling user username is incorrect
            }
            if (!e.equals(null)) {
                if (passwordTextField.getText().equals(e.getPassword())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../POSScene.fxml"));
                    BorderPane root = loader.load();
                    POSController posController = loader.getController();
                    Scene posScene = new Scene(root, 600, 600);

                    posController.setPrimaryStage(primaryStage, tmp, this, employeeCollection, currentSession);
                    primaryStage.setMaxWidth(600);
                    primaryStage.setMaxHeight(600);
                    primaryStage.setScene(posScene);
                } else {
                    //Dialog telling user password is incorrect
                }
            }
        }
    }




}
