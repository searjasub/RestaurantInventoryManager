package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Employee;
import model.OrderedItem;
import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static javafx.collections.FXCollections.observableArrayList;

public class FinanceController {

    private MenuBar menuBar;
    private TableView table = initTable();
    private ObservableList<FinanceItem> master = observableArrayList();
    private ObservableList<OrderedItem> ordered = observableArrayList();
    private InventoryTrackerController tracker = new InventoryTrackerController();
    private MongoClient mc = new MongoClient("localHost");
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Inventory");
    private DB db = mc.getDB("Restaurants");
    private DBCollection empsCollection = db.getCollection("Employees");
    private DBCollection adminCollection = db.getCollection("Administrators");
    private DBCollection invCollection = db.getCollection("Inventory");
    private DBCollection investCollection = db.getCollection("Investments");
    private DBCollection expensesCollection = db.getCollection("Expenses");
    private Stage primaryStage;
    private EmployeesController employeesController;
    private Scene financeScene;
    private MainStageController mainStageController;
    private Pagination myPagination;
    private CurrentSession currentSession;

    void setPrimaryStage(Stage primaryStage, Scene financeScene, MainStageController mainStageController,
                         HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.primaryStage = primaryStage;
        this.financeScene = financeScene;
        this.mainStageController = mainStageController;
        this.currentSession = currentSession;
        this.primaryStage.setTitle("Restaurant Inventory Manager - Finance");

        Menu viewMenu = new Menu("View");
        RadioMenuItem administrators = new RadioMenuItem("Administrators");
        RadioMenuItem employees = new RadioMenuItem("Employees");
        RadioMenuItem inventory = new RadioMenuItem("Inventory");
        RadioMenuItem pos = new RadioMenuItem("POS");
        RadioMenuItem finance = new RadioMenuItem("Finance");

        viewMenu.getItems().add(administrators);
        viewMenu.getItems().add(employees);
        viewMenu.getItems().add(inventory);
        viewMenu.getItems().add(pos);
        viewMenu.getItems().add(finance);

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(administrators);
        toggleGroup.getToggles().add(employees);
        toggleGroup.getToggles().add(inventory);
        toggleGroup.getToggles().add(pos);
        toggleGroup.getToggles().add(finance);

        Menu financeMenu = new Menu("Finance View");
        RadioMenuItem assets = new RadioMenuItem("Assets");
        RadioMenuItem liabilities = new RadioMenuItem("Liabilities");
        RadioMenuItem capital = new RadioMenuItem("Capital");
        RadioMenuItem net = new RadioMenuItem("Net Worth");

        ToggleGroup financeGroup = new ToggleGroup();
        financeGroup.getToggles().addAll(assets, liabilities, capital, net);

        this.menuBar.getMenus().add(viewMenu);
        this.menuBar.getMenus().add(financeMenu);

        administrators.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdminView.fxml"));
            BorderPane root;
            Scene adminScene = null;
            try {
                root = loader.load();
                adminScene = new Scene(root, 600, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AdminController adminController = loader.getController();

            adminController.setPrimaryStage(primaryStage, adminScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMinHeight(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(adminScene);
        });

        employees.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../EmployeesView.fxml"));
            BorderPane root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            EmployeesController adminController = loader.getController();
            inventory.setSelected(true);
            adminController.setPrimaryStage(primaryStage, financeScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(new Scene(Objects.requireNonNull(root), 600, 600));
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
            posController.setPrimaryStage(primaryStage, posScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(posScene);

        });

        assets.setOnAction(event -> {
            master = getFinances();

            ObservableList<FinanceItem> assetList = FXCollections.observableArrayList();

            for(FinanceItem item : master){
                if(item.getType() == FinanceItem.financeType.ASSET){
                    assetList.add(item);
                }
            }

            table.getItems().clear();
            table.refresh();
            master = null;
            master = assetList;
            table.getItems().addAll(master);
            table.refresh();

        });

        liabilities.setOnAction(event -> {
            master = getFinances();

            ObservableList<FinanceItem> liabilityList = FXCollections.observableArrayList();

            for(FinanceItem item : master){
                if(item.getType() == FinanceItem.financeType.LIABILITY){
                    liabilityList.add(item);
                }
            }

            table.getItems().clear();
            table.refresh();
            master = null;
            master = liabilityList;
            table.getItems().addAll(master);
            table.refresh();
        });

        capital.setOnAction(event -> {
            master = getFinances();

            ObservableList<FinanceItem> capitalList = FXCollections.observableArrayList();

            for(FinanceItem item : master){
                if(item.getType() == FinanceItem.financeType.CAPITAL){
                    capitalList.add(item);
                }
            }

            table.getItems().clear();
            table.refresh();
            master = null;
            master = capitalList;
            table.getItems().addAll(master);
            table.refresh();
        });

        net.setOnAction(event -> {
            table.getItems().clear();
            table.refresh();
            master = null;
            master = getFinances();
            table.getItems().addAll(master);
            table.refresh();
        });
    }

    private TableView<FinanceItem> initTable() {
        table = new TableView<FinanceItem>();
        table.setEditable(true);

        TableColumn<FinanceItem, Integer> id = new TableColumn<>();
        id.setText("ID");

        //TableColumn<FinanceItem, String> name = new TableColumn<>();


        return table;
    }

    public void init() {
        // Method to get all inventory items and set them to the ObservableList needed
        ordered = tracker.reviewOrderedItems();
        myPagination.setPageFactory(this::createPage);
    }

    public ObservableList<FinanceItem> getFinances(){
        ObservableList<FinanceItem> financeItems = FXCollections.observableArrayList();

        DBCursor cursor = empsCollection.find();
        List<DBObject> list = cursor.toArray();

        for(DBObject obj : list){
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("employeeID").toString());
            f.setName(obj.get("name").toString());
            f.setCost("8");
            f.setAmount("1");
            f.setType(FinanceItem.financeType.LIABILITY);
            financeItems.add(f);
        }


        cursor = invCollection.find();
        list = cursor.toArray();

        for(DBObject obj : list){
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("ingredientID").toString());
            f.setName(obj.get("name").toString());
            f.setCost(obj.get("wholesale").toString());
            f.setAmount(obj.get("amount").toString());
            f.setType(FinanceItem.financeType.ASSET);
            financeItems.add(f);
        }

        cursor = expensesCollection.find();
        list = cursor.toArray();

        for(DBObject obj : list){
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("expenseID").toString());
            f.setName(obj.get("name").toString());
            f.setCost(obj.get("cost").toString());
            f.setAmount("1");
            f.setType(FinanceItem.financeType.LIABILITY);
            financeItems.add(f);
        }

       cursor = adminCollection.find();
        list = cursor.toArray();

        for(DBObject obj : list){
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("employeeID").toString());
            f.setName(obj.get("name").toString());
            f.setCost("15");
            f.setAmount("1");
            f.setType(FinanceItem.financeType.LIABILITY);
            financeItems.add(f);
        }

      cursor = investCollection.find();
        list = cursor.toArray();

        for(DBObject obj : list){
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("investmentID").toString());
            f.setName(obj.get("name").toString());
            f.setCost(obj.get("cost").toString());
            f.setAmount("1");
            f.setType(FinanceItem.financeType.CAPITAL);
            financeItems.add(f);
        }

        return financeItems;
    }

    private Node createPage(int pageIndex) {
        int pageSize = 50;
        int first = pageIndex * pageSize;
        int last = Math.min(first + pageSize, master.size());
        table.getItems().add(observableArrayList(master.subList(first, last)));

        return table;
    }

    public void onMenuItemExit(ActionEvent actionEvent) {
        primaryStage.close();
    }

    // Methods to add:

    // Method(s) that allow the user to switch between seeing only Assets,
    // Liabilities, and Capital investments as well as a view that allows them to
    // see all three
    // Should be in chronological order

    // Method that allows the user to click on the name of an item, and then view
    // all items of that type along with their asset/liability values and the dates
    // they were put in and their expiration date

    //TODO MAKE CLASSES FOR THIS
    public static class FinanceItem {
        private String id;
        private String name;
        private String amount;
        private String cost;
        private financeType type;

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public financeType getType() {
            return type;
        }

        public void setType(financeType type) {
            this.type = type;
        }

        public enum financeType {
            ASSET, LIABILITY, CAPITAL
        }


    }

}
