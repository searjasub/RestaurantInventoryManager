package controller;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.ObservableList;
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
import models.Ingredient;
import models.OrderedItem;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;


public class InventoryTrackerController {

    public MenuBar menu;
    private MongoClient mc = new MongoClient("localHost");
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Inventory");
    private Block<Document> printBlock = System.out::println;
    private AdministrativeController controller;
    private Stage primaryStage;
    private Scene inventoryScene;
    private MainStageController mainController;
    private RadioMenuItem admin;
    private RadioMenuItem inventory;
    private RadioMenuItem pos;
    private RadioMenuItem finance;


    void setPrimaryScene(Stage primaryStage, Scene inventoryScene, MainStageController mainController, RadioMenuItem admin, RadioMenuItem inventory, RadioMenuItem pos, HashMap<Integer, Employee> employeesCollection) {
        this.primaryStage = primaryStage;
        this.inventoryScene = inventoryScene;
        this.mainController = mainController;
        this.admin = admin;
        this.inventory = inventory;
        this.pos = pos;

        menu = new MenuBar();


        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(this.admin);
        toggleGroup.getToggles().add(this.inventory);
        toggleGroup.getToggles().add(this.pos);
//        toggleGroup.getToggles().add(this.finance);

        Menu menuOptions = new Menu();
        menuOptions.getItems().add(admin);
        menuOptions.getItems().add(inventory);
        menuOptions.getItems().add(pos);
//        menuOptions.getItems().add(finance);

        menu.getMenus().add(menuOptions);
        primaryStage.setTitle("Restaurant Inventory Manager - Inventory Tracker");

        admin.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdministrativeScene.fxml"));
            BorderPane root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AdministrativeController adminController = loader.getController();
            inventory.setSelected(true);
            adminController.setPrimaryStage(primaryStage, inventoryScene, mainController, employeesCollection, admin);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(new Scene(root, 600, 600));
        });

        pos.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../POSScene.fxml"));
            Scene posScene = null;
            BorderPane root;
            try {
                root = loader.load();
                posScene = new Scene(root, 600, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
            POSController posController = loader.getController();
            pos.setSelected(true);
            posController.setPrimaryStage(primaryStage, posScene, mainController, admin, inventory, pos, employeesCollection);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(posScene);

        });
    }

    public void addItem(String ingredientName, int itemId, Date prepDate, Date expDate, int caloriePerServing,
                        int amount, int individualCost, int bulkCost, int bulkAmount) {
        collection.insertOne(new Document("ingredientName", ingredientName).append("ingredientID", itemId).append("prepDate", prepDate)
                .append("expDate", expDate).append("caloriePerServing", caloriePerServing).append("amount", amount).append("individualCost", individualCost)
                .append("bulkCost", bulkCost).append("bulkAmount", bulkAmount));
    }

    public void deleteItem(String itemName) {
        collection.deleteOne(eq("name", itemName));
    }

    public void updateItem(int itemId, String updateField, String updateValue) {
        collection.updateOne(eq("ingredientID", itemId), new Document("$set", new Document(updateField, updateValue)));
    }

    public void getItemData(String itemName) {
        collection.find(eq("name", itemName)).forEach(printBlock);
    }

    public ArrayList<Ingredient> soonToExpire() {
        return null;
    }

    public void retrieveDailySales() {

    }

    public void retrieveWeeklySales(Date startDay) {

    }

    public ObservableList<OrderedItem> reviewOrderedItems() {
        return null;
    }

    public void onMenuItemExit(ActionEvent actionEvent) {

    }


}
