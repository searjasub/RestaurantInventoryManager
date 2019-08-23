package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Employee;
import org.bson.Document;

import java.io.IOException;
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
    private MongoClient mc = new MongoClient();
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Employees");
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection employeesCollection = db.getCollection("Employees");
    private HashMap<Integer, Employee> adminMap = new HashMap<>();
    private MongoCollection<Document> adminCollection = database.getCollection("Administrators");
    private DBCollection adminDbCollection = db.getCollection("Administrators");
    private CurrentSession currentSession = new CurrentSession();

    public void setPrimaryStage(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.scene = scene;
        employeeCollection = fillEmpCollection();
        adminMap = fillAdminCollection();

        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        passwordField.managedProperty().bind(checkbox.selectedProperty());
        passwordField.visibleProperty().bind(checkbox.selectedProperty());
        passwordTextField.managedProperty().bind(checkbox.selectedProperty().not());
        passwordTextField.visibleProperty().bind(checkbox.selectedProperty().not());
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    public void login() throws IOException {

        Employee employee;
        if (usernameTextField.getText().startsWith("3")) {
            int id = 0;
            try {
                id = Integer.parseInt(usernameTextField.getText());
            } catch (NumberFormatException ex) {
                showAlertInvalidInput();
            }
            employee = adminMap.get(id);
            if (employee != null) {
                if (passwordTextField.getText().equals(employee.getPassword())) {
                    currentSession.setAdmin(true);
                    currentSession.setLoggedIn(employee);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdministrativeScene.fxml"));
                    BorderPane root = loader.load();
                    AdministrativeController adminController = loader.getController();
                    Scene administrativeScene = new Scene(root, 600, 600);

                    adminController.setPrimaryStage(primaryStage, scene, this, employeeCollection, currentSession);
                    primaryStage.setMaxWidth(600);
                    primaryStage.setMaxHeight(600);
                    primaryStage.setScene(administrativeScene);
                } else {
                    showAlertInvalidInput();
                }
            }
        } else {
            int id = 0;
            try {
                id = Integer.parseInt(usernameTextField.getText());
            } catch (NumberFormatException ex) {
                showAlertInvalidInput();
            }
            employee = employeeCollection.get(id);
            if (employee != null) {
                if (passwordTextField.getText().equals(employee.getPassword())) {
                    currentSession.setAdmin(false);
                    currentSession.setLoggedIn(employee);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../POSScene.fxml"));
                    BorderPane root = loader.load();
                    POSController posController = loader.getController();
                    Scene posScene = new Scene(root, 600, 600);

                    posController.setPrimaryStage(primaryStage, scene, this, employeeCollection, currentSession);
                    primaryStage.setMaxWidth(600);
                    primaryStage.setMaxHeight(600);
                    primaryStage.setScene(posScene);
                } else {
                    showAlertInvalidInput();
                }
            }
        }
    }

    private void showAlertInvalidInput() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid ID/Password, please try again.", ButtonType.OK);
        alert.setTitle("Invalid Input");
        alert.show();
    }

    private HashMap<Integer, Employee> fillEmpCollection() {
        return generateHashMap(employeesCollection);
    }

    private HashMap<Integer, Employee> generateHashMap(DBCollection collection) {
        HashMap<Integer, Employee> data = new HashMap<>();
        List<DBObject> dbObjects;
        DBCursor cursor = collection.find();
        dbObjects = cursor.toArray();
        Employee employee;
        for (DBObject dbObject : dbObjects) {
            employee = getEmployee(dbObject);
            data.put(Integer.parseInt(employee.getId()), employee);
        }
        return data;
    }

    static Employee getEmployee(DBObject dbObject) {
        Employee employee;
        employee = new Employee();
        employee.setName(dbObject.get("name").toString());
        employee.setPassword(dbObject.get("password").toString());
        employee.setOccupation(dbObject.get("occupation").toString());
        employee.setWeeklyHours(dbObject.get("weeklyHours").toString());
        employee.setId(dbObject.get("employeeID").toString());
        employee.setHourlyPay(dbObject.get("hourlyPay").toString());
        return employee;
    }

    private HashMap<Integer, Employee> fillAdminCollection() {
        return generateHashMap(adminDbCollection);
    }
}
