package controller;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Employee;
import model.Meal;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class POSController {

    private static double salesTax;
    public BorderPane borderPane;
    public MenuBar menuBar;
    private MongoClient mc = new MongoClient();
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Meals");
    private ArrayList<Meal> meals = new ArrayList<Meal>();
    private double totalCost;
    private double tip;
    private int orderNumber;
    private Stage primaryStage;
    private Scene scene;
    private MainStageController mainController;
    private HashMap<Integer, Employee> empsCollection;
    private RadioMenuItem admin;
    private RadioMenuItem inventory;
    private RadioMenuItem pos;

    private TableView<String> mealTable = new TableView<>();

    public static double getSalesTax() {
        return salesTax;
    }

    public static void setSalesTax(double salesTax) {
        POSController.salesTax = salesTax;
    }

//    void setPrimaryStage(Stage primaryStage, Scene tmp, MainStageController mainStageController) {
//        this.primaryStage = primaryStage;
//        this.scene = tmp;
//        this.mainController = mainStageController;
//
//        primaryStage.setTitle("Inventory Tracker Manager - POS");
//    }


    void setPrimaryStage(Stage primaryStage, Scene posScene, MainStageController mainStageController, HashMap<Integer, Employee> employeesCollection, boolean isAdmin) {
        this.primaryStage = primaryStage;
        this.scene = posScene;
        this.mainController = mainStageController;
        this.empsCollection = employeesCollection;
        this.primaryStage.setTitle("Inventory Tracker Manager - POS");

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

            pos.setSelected(true);

            mealTable.setItems(fillMealCollection());
            this.menuBar.getMenus().add(viewMenu);

            admin.setOnAction(event -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdministrativeScene.fxml"));
                BorderPane root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AdministrativeController adminController = loader.getController();
                admin.setSelected(true);
                adminController.setPrimaryStage(primaryStage, posScene, mainController, employeesCollection, true);
                primaryStage.setMaxWidth(600);
                primaryStage.setMaxHeight(600);
                primaryStage.setScene(new Scene(root, 600, 600));
            });

            inventory.setOnAction(event -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../InventoryTrackerScene.fxml"));
                BorderPane root;
                Scene scene = null;
                try {
                    root = loader.load();
                    scene = new Scene(root, 600, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InventoryTrackerController inventoryController = loader.getController();

                inventory.setSelected(true);
                inventoryController.setPrimaryScene(primaryStage, scene, mainStageController, employeesCollection);
                primaryStage.setMaxWidth(600);
                primaryStage.setMaxHeight(600);
                primaryStage.setScene(scene);
            });
        }

    }

    private ObservableList<String> fillMealCollection() {
        ObservableList<String> emps = FXCollections.observableArrayList();
        int id = 100001;

        for(int i = 0; i < 4; i++){
            String idString = ""+id+"";
            String meal = collection.find(eq("id", idString)).toString();
            emps.add(meal);
            id++;
        }
        return emps;
    }

    public void splitTab() {

    }

    public void copyMeal(int mealId) {

    }

    public void updateMeal(int mealId, String updateField, String updateValue) {
        collection.updateOne(eq("mealID", mealId), new Document("$set", new Document(updateField, updateValue)));
    }

    public void deleteMeal(int mealId) {
        collection.deleteOne(eq("mealID", mealId));
    }

    public void addMealToOrder(int mealId) {

    }

    public void calculateFinalCost() {

    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void onMenuItemExit(ActionEvent actionEvent) {
        primaryStage.close();
    }


}
