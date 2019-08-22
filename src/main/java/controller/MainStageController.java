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
    private MongoClient mc = new MongoClient();
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Employees");
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection dbCollection = db.getCollection("Employees");
    private HashMap<Integer, Employee> adminMap = new HashMap<>();
    private MongoCollection<Document> adminCollection = database.getCollection("Administrators");
    private DBCollection adminDbCollection = db.getCollection("Administrators");
    private CurrentSession currentSession = new CurrentSession();

    public MainStageController() {

    }

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
            //TODO finish
            if (e.equals(null)) {
                //Dialog telling user username is not valid
            } else if (!e.equals(null)) {
                if (passwordTextField.getText().equals(e.getPassword())) {
                    currentSession.setAdmin(true);
                    currentSession.setLoggedIn(e);
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

    private HashMap<Integer, Employee> fillEmpCollection() {
        HashMap<Integer, Employee> data = new HashMap<>();
        List<DBObject> dbObjects = new ArrayList<>();
        int id = 100001;
        for (int i = 0; i < 5; i++) {
            DBObject query = BasicDBObjectBuilder.start().add("employeeID", id + i).get();
            DBCursor cursor = dbCollection.find(query);
            while (cursor.hasNext()) {
                dbObjects.add(cursor.next());
            }
        }

        Employee employee;
        for (int i = 0; i < dbObjects.size(); i++) {
            employee = new Employee();
            employee.setName(dbObjects.get(i).get("name").toString());
            employee.setPassword(dbObjects.get(i).get("password").toString());
            employee.setOccupation(dbObjects.get(i).get("occupation").toString());
            employee.setWeeklyHours(dbObjects.get(i).get("weeklyHours").toString());
            employee.setId(dbObjects.get(i).get("employeeID").toString());
            employee.setHourlyPay(dbObjects.get(i).get("hourlyPay").toString());
            data.put(Integer.parseInt(employee.getId()), employee);
        }
        return data;
    }

    private HashMap<Integer, Employee> fillAdminCollection() {
        HashMap<Integer, Employee> data = new HashMap<>();

        List<DBObject> dbObjects = new ArrayList<>();

//        int id = 30000;
//        for (int i = 0; i < adminCollection.countDocuments(); i++) {
//            DBObject query1 = BasicDBObjectBuilder.start().add("employeeID", "" + (id + i)).get();
//            DBCursor cursor = adminDbCollection.find(query1);
//            while (cursor.hasNext()) {
//                dbObjects.add(cursor.next());
//            }
//        }
        DBCursor cursor = adminDbCollection.find();

        dbObjects = cursor.toArray();

        System.out.println(dbObjects);

        Employee employee;
        for (int i = 0; i < adminCollection.countDocuments(); i++) {
            employee = new Employee();
            employee.setName(dbObjects.get(i).get("name").toString());
            employee.setPassword(dbObjects.get(i).get("password").toString());
            employee.setOccupation(dbObjects.get(i).get("occupation").toString());
            employee.setWeeklyHours(dbObjects.get(i).get("weeklyHours").toString());
            employee.setId(dbObjects.get(i).get("employeeID").toString());
            employee.setHourlyPay(dbObjects.get(i).get("hourlyPay").toString());
            data.put(Integer.parseInt(employee.getId()), employee);
        }
        return data;
    }
}
