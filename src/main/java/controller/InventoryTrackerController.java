package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Employee;
import model.Ingredient;
import model.OrderedItem;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class InventoryTrackerController {

    public MenuBar menu;
    public Pagination inventoryPagination;

    private MongoClient mc = new MongoClient("localHost");
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Inventory");
    private Block<Document> printBlock = System.out::println;
    private AdministrativeController controller;
    private Stage primaryStage;
    private Scene inventoryScene;
    private MainStageController mainController;
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection dbCollection = db.getCollection("Inventory");

    private ObservableList<Ingredient> data = fillIngredientCollection();
    private TableView<Ingredient> ingredientTable = createTable();

    void setPrimaryScene(Stage primaryStage, Scene inventoryScene, MainStageController mainController, HashMap<Integer, Employee> employeesCollection) {
        this.primaryStage = primaryStage;
        this.inventoryScene = inventoryScene;
        this.mainController = mainController;
        primaryStage.setTitle("Restaurant Inventory Manager - Inventory Tracker");


        if (data.size() > 100) {
            inventoryPagination.setPageCount((data.size() / 100) + 1);
        } else {
            inventoryPagination.setPageCount(1);
        }
        inventoryPagination.setPageFactory(this::createPage);



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

        inventory.setSelected(true);

//        ingredientTable.setItems(fillInventoryCollection());


        menu.getMenus().add(viewMenu);

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
            adminController.setPrimaryStage(primaryStage, inventoryScene, mainController, employeesCollection, true);
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
            posController.setPrimaryStage(primaryStage, posScene, mainController, employeesCollection, true);
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

//    private ObservableList<String> fillInventoryCollection() {
//        ObservableList<String> inventory = FXCollections.observableArrayList();
//        int id = 500001;
//
//        for(int i = 0; i < 4; i++){
//            String idString = ""+id+"";
//            String item = collection.find(eq("id", idString)).toString();
//            inventory.add(item);
//            id++;
//        }
//        return inventory;
//    }

    public void retrieveDailySales() {

    }

    public void retrieveWeeklySales(Date startDay) {

    }

    public ObservableList<OrderedItem> reviewOrderedItems() {
        return null;
    }

    public void onMenuItemExit(ActionEvent actionEvent) {

    }
    private TableView<Ingredient> createTable() {

        ingredientTable = new TableView<>();
        ingredientTable.setEditable(true);

        TableColumn<Ingredient, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(event -> event.getTableView().getItems()
                .get(event.getTablePosition().getRow()).setName(event.getNewValue()));

        TableColumn<Ingredient, String> ingredientId = new TableColumn<>("Ingredient ID");

        TableColumn<Ingredient, String> amount = new TableColumn<>("Amount");

        TableColumn<Ingredient, String> prepDate = new TableColumn<>("Prep Date");

        TableColumn<Ingredient, String> expiredDate = new TableColumn<>("Expired Date");

        TableColumn<Ingredient, String> veganFriendly = new TableColumn<>("Vegan Friendly");

        TableColumn<Ingredient, String> caloriePerServing = new TableColumn<>("Calories Per Serving");

        TableColumn<Ingredient, String> costPerIngredient = new TableColumn<>("Cost Per Ingredient");

        TableColumn<Ingredient, String> bulkCost = new TableColumn<>("Bulk Cost");


        ingredientTable.getColumns().setAll(name, ingredientId, amount, prepDate, expiredDate, veganFriendly, caloriePerServing, costPerIngredient, bulkCost);

        return ingredientTable;

    }

    private Node createPage(Integer pageIndex) {
        int rowsPerPage = 10;
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, (int)collection.countDocuments());
        ingredientTable.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return ingredientTable;
    }
    private ObservableList<Ingredient> fillIngredientCollection() {
        ObservableList<Ingredient> data = FXCollections.observableArrayList();

        List<DBObject> dbObjects = new ArrayList<>();

        int id = 500001;
        for (int i = 0; i < collection.countDocuments(); i++) {
            DBObject query = BasicDBObjectBuilder.start().add("ingredientID", id + i).get();
            DBCursor cursor = dbCollection.find(query);
            while (cursor.hasNext()) {
                dbObjects.add(cursor.next());
            }
        }



        Ingredient ingredient;
        for (int i = 0; i < collection.countDocuments(); i++) {

            ingredient = new Ingredient();
            ingredient.setName(dbObjects.get(i).get("name").toString());
//            employee.setPassword(dbObjects.get(i).get("password").toString());
//            employee.setOccupation(dbObjects.get(i).get("occupation").toString());
//            employee.setWeeklyHours(dbObjects.get(i).get("weeklyHours").toString());
//            employee.setId(dbObjects.get(i).get("employeeID").toString());
//            employee.setHourlyPay(dbObjects.get(i).get("hourlyPay").toString());
            data.add(ingredient);

        }
        return data;
    }
    }
