package controller;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Employee;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class AdministrativeController {

    public MenuBar menuBar = new MenuBar();
    public RadioButton radioAll;
    public RadioButton radioClockedIn;

    private MongoClient mc = new MongoClient();
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Employees");

    private HashMap<Integer, Employee> empsCollection;
    private Stage primaryStage;
    private Scene adminScene;
    private MainStageController mainController;

    void setPrimaryStage(Stage primaryStage, Scene adminScene, MainStageController mainStageController, HashMap<Integer, Employee> employeesCollection, boolean isAdmin) {
        this.primaryStage = primaryStage;
        this.adminScene = adminScene;
        this.mainController = mainStageController;
        this.empsCollection = employeesCollection;
        primaryStage.setTitle("Restaurant Inventory Manager - Administrator");

        if (isAdmin) {

            Menu viewMenu = new Menu("View");
            RadioMenuItem admin = new RadioMenuItem("Admin");
            RadioMenuItem inventory = new RadioMenuItem("Inventory");
            RadioMenuItem pos = new RadioMenuItem("POS");
            RadioMenuItem finance = new RadioMenuItem("Finance");

            viewMenu.getItems().add(admin);
            viewMenu.getItems().add(inventory);
            viewMenu.getItems().add(pos);
            viewMenu.getItems().add(finance);

            ToggleGroup toggleGroup = new ToggleGroup();
            toggleGroup.getToggles().add(admin);
            toggleGroup.getToggles().add(inventory);
            toggleGroup.getToggles().add(pos);
            toggleGroup.getToggles().add(finance);
            admin.setSelected(true);

            Menu employeesMenu = new Menu("Employees");
            MenuItem addEmployee = new Menu("Add");
            MenuItem deleteEmployee = new Menu("Delete");
            employeesMenu.getItems().add(addEmployee);
            employeesMenu.getItems().add(deleteEmployee);

            menuBar.getMenus().add(viewMenu);
            menuBar.getMenus().add(employeesMenu);

			ToggleGroup radioToggleGroup = new ToggleGroup();
			radioToggleGroup.getToggles().add(radioAll);
			radioToggleGroup.getToggles().add(radioClockedIn);
            radioAll.setSelected(true);

			inventory.setOnAction(event -> {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("../InventoryTrackerScene.fxml"));
				BorderPane root;
				Scene administrativeScene = null;
				try {
					root = loader.load();
					administrativeScene = new Scene(root, 600, 600);
				} catch (IOException e) {
					e.printStackTrace();
				}
				InventoryTrackerController inventoryController = loader.getController();

				inventory.setSelected(true);
				inventoryController.setPrimaryScene(primaryStage, administrativeScene, mainStageController, employeesCollection);
				primaryStage.setMaxWidth(600);
				primaryStage.setMaxHeight(600);
				primaryStage.setScene(administrativeScene);
			});

			finance.setOnAction(event -> {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("../FinanceScene.fxml"));
				BorderPane root;
				Scene financeScene = null;
				try {
					root = loader.load();
					financeScene = new Scene(root, 600, 600);
				} catch (IOException e) {
					e.printStackTrace();
				}
				FinanceController financeController = loader.getController();

				financeController.setPrimaryScene(primaryStage, financeScene, mainStageController, employeesCollection);
				primaryStage.setMaxWidth(600);
				primaryStage.setMaxHeight(600);
				primaryStage.setScene(financeScene);

			});


			pos.setOnAction(event -> {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("../POSScene.fxml"));
				BorderPane root;
				Scene posScene = null;
				try {
					root = loader.load();
					posScene = new Scene(root, 600, 600);
				} catch (IOException e) {
					e.printStackTrace();
				}
				POSController posController = loader.getController();
				posController.setPrimaryStage(primaryStage, posScene, mainStageController, employeesCollection);
				primaryStage.setMaxWidth(600);
				primaryStage.setMaxHeight(600);
				primaryStage.setScene(posScene);
			});

        } else {


        }
    }


    public void AddEmployee(Employee e) {
        collection.insertOne(new Document("name", e.getName()).append("employeID", e.getId()).append("password", e.getPassword())
                .append("hourlyPay", e.getHourlyPay()).append("occupation", e.getOccupation()));
    }

    public void deleteEmployee(int id) {
        collection.deleteOne(eq("employeID", id));
    }

    public void updateEmployee(int id, String updateField, String updateValue) {
        collection.updateOne(eq("employeID", id), new Document("$set", new Document(updateField, updateValue)));
    }

    public void startBreak(Date time, int id) {
        collection.updateOne(eq("employeID", id), new Document("$set", new Document("breakStart", time)));
    }

    public void endBreak(Date time, int id) {
        collection.updateOne(eq("employeID", id), new Document("$set", new Document("breakEnd", time)));
    }

    public void clockIn(int id, Date time) {
        collection.updateOne(eq("employeID", id), new Document("$set", new Document("clockIn", time)));
    }

    public void clockOut(int id, Date time) {
        collection.updateOne(eq("employeID", id), new Document("$set", new Document("clockOut", time)));
    }

    public void login(int id, String password) {
        BasicDBObject andQuery = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("employeID", id));
        obj.add(new BasicDBObject("password", password));
        andQuery.put("$and", obj);
        collection.find(andQuery);
    }

    public void fillEmpCollection() {

    }


    public void onAddEmployeeButton() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Contact Dialog");
        dialog.setHeaderText("Please Input Employee Data");

        ButtonType loginButtonType = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField password = new TextField();
        password.setPromptText("Password");
        TextField occupation = new TextField();
        occupation.setPromptText("Occupation");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("Primary Email:"), 0, 2);
        grid.add(occupation, 1, 2);

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> name.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Employee e = new Employee();

                e.setName(name.getText().trim());
                e.setPassword(password.getText().trim());
                e.setOccupation(occupation.getText().trim());

                return e;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();


        if (result.isPresent()) {
            AddEmployee(result.get());
        }

    }


    public void onMenuItemExit(ActionEvent actionEvent) {
        primaryStage.close();
    }

    public void allShow(ActionEvent actionEvent) {

    }
}
