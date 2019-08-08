package controller;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.Employee;
import models.Meal;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class POSController {

    private static double salesTax;
    public BorderPane borderPane;
    private MenuBar menu;
    private Menu menuOptions;
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

    public static double getSalesTax() {
        return salesTax;
    }

    public static void setSalesTax(double salesTax) {
        POSController.salesTax = salesTax;
    }

    public void setPrimaryStage(Stage primaryStage, Scene tmp, MainStageController mainStageController) {
        this.primaryStage = primaryStage;
        this.scene = tmp;
        this.mainController = mainStageController;

        primaryStage.setTitle("Inventory Tracker Manager - POS");
    }


    void setPrimaryStage(Stage primaryStage, Scene posScene, MainStageController mainStageController, RadioMenuItem admin, RadioMenuItem inventory, RadioMenuItem pos, Menu menuOptions, MenuBar menu, HashMap<Integer, Employee> employeesCollection) {
        this.primaryStage = primaryStage;
        this.scene = posScene;
        this.mainController = mainStageController;
        this.admin = admin;
        this.inventory = inventory;
        this.pos = pos;
        this.empsCollection = employeesCollection;
        this.menuOptions = menuOptions;
        this.menu = menu;
        primaryStage.setTitle("Inventory Tracker Manager - POS");

        Menu options = new Menu("View");
        ToggleGroup toggleGroup = new ToggleGroup();
        pos.setSelected(true);
        toggleGroup.getToggles().add(admin);
        toggleGroup.getToggles().add(inventory);
        toggleGroup.getToggles().add(pos);

        menuOptions.getItems().add(admin);
        menuOptions.getItems().add(inventory);
        menuOptions.getItems().add(pos);

        menu.getMenus().add(options);

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
            adminController.setPrimaryStage(primaryStage, posScene, mainController, employeesCollection, admin);
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
            inventoryController.setPrimaryScene(primaryStage, scene, mainStageController, admin, inventory, pos, menuOptions, employeesCollection);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(scene);
        });

        admin.setOnAction(event -> {

        });

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
