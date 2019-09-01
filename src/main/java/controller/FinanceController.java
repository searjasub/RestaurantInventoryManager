package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import enums.FinanceType;
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
import model.FinanceItem;
import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static javafx.collections.FXCollections.observableArrayList;

public class FinanceController {

    @FXML
    private Pagination pagination;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Label countLabel;
    private TableView<FinanceItem> table = createTable();
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
    private ObservableList<FinanceItem> master = getFinances();
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

        if (master.size() > 10) {
            pagination.setPageCount((master.size() / 10));
        } else {
            pagination.setPageCount(1);
        }
        pagination.setPageFactory(this::createPage);

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
        finance.setSelected(true);

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

        financeMenu.getItems().addAll(assets, liabilities, capital, net);

        ToggleGroup financeGroup = new ToggleGroup();
        financeGroup.getToggles().addAll(assets, liabilities, capital, net);

        this.menuBar.getMenus().add(viewMenu);
        this.menuBar.getMenus().add(financeMenu);

        financeGroup.selectToggle(net);

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

            int value = 0;

            for (FinanceItem item : master) {
                if (item.getType() == FinanceType.ASSET) {
                    assetList.add(item);

                    value += Integer.parseInt(item.getCost());
                }
            }

            this.countLabel.setText("Total Asset Value: " + value);

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

            int value = 0;
            for (FinanceItem item : master) {
                if (item.getType() == FinanceType.LIABILITY) {
                    liabilityList.add(item);

                    value += Integer.parseInt(item.getCost());
                }
            }

            this.countLabel.setText("Total Value of Liabilities: " + value);

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

            int value = 0;
            for (FinanceItem item : master) {
                if (item.getType() == FinanceType.CAPITAL) {
                    capitalList.add(item);

                    value += Integer.parseInt(item.getCost());
                }
            }

            this.countLabel.setText("Total Capital: " + value);
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
            int value = 0;
            for(FinanceItem item : master){
                if(item.getType() == FinanceType.LIABILITY){
                    value -= Integer.parseInt(item.getCost());
                }
                else {
                    value += Integer.parseInt(item.getCost());
                }
            }
            this.countLabel.setText("Total Net Worth: " + value);
            table.getItems().addAll(master);
            table.refresh();
        });
    }

    private TableView<FinanceItem> createTable() {
        table = new TableView<>();
        table.setEditable(false);

        TableColumn<FinanceItem, String> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<FinanceItem, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<FinanceItem, String> cost = new TableColumn<>("Cost");
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        cost.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<FinanceItem, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amount.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<FinanceItem, String> type = new TableColumn<>("Type");
        type.setCellValueFactory(new PropertyValueFactory<>("stringType"));
        type.setCellFactory(TextFieldTableCell.forTableColumn());

        table.getColumns().setAll(name,id,cost,amount, type);

        return table;
    }

    private ObservableList<FinanceItem> getFinances() {
        ObservableList<FinanceItem> financeItems = FXCollections.observableArrayList();

        DBCursor cursor = empsCollection.find();
        List<DBObject> dbObjectsEmp = cursor.toArray();


        for (DBObject obj : dbObjectsEmp) {
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("employeeID").toString());
            f.setName(obj.get("name").toString());
            f.setCost("8");
            f.setAmount("1");
            f.setType(FinanceType.LIABILITY);
            financeItems.add(f);
        }


        cursor = invCollection.find();
        List<DBObject> dbObjectsInventory = cursor.toArray();

        for (DBObject obj : dbObjectsInventory) {
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("ingredientID").toString());
            f.setName(obj.get("name").toString());
            f.setCost(obj.get("wholesale").toString());
            f.setAmount(obj.get("amount").toString());
            f.setType(FinanceType.ASSET);
            financeItems.add(f);
        }

        cursor = expensesCollection.find();
        List<DBObject> dbObjectsExpense = cursor.toArray();

        for (DBObject obj : dbObjectsExpense) {
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("expenseID").toString());
            f.setName(obj.get("name").toString());
            f.setCost(obj.get("cost").toString());
            f.setAmount("1");
            f.setType(FinanceType.LIABILITY);
            financeItems.add(f);
        }

        cursor = adminCollection.find();
        List<DBObject> dbObjectsAdmin = cursor.toArray();

        for (DBObject obj : dbObjectsAdmin) {
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("employeeID").toString());
            f.setName(obj.get("name").toString());
            f.setCost("15");
            f.setAmount("1");
            f.setType(FinanceType.LIABILITY);
            financeItems.add(f);
        }

        cursor = investCollection.find();
        List<DBObject> dbObjectsInvest = cursor.toArray();

        for (DBObject obj : dbObjectsInvest) {
            FinanceItem f = new FinanceItem();
            f.setId(obj.get("investmentID").toString());
            f.setName(obj.get("investorName").toString());
            f.setCost(obj.get("cost").toString());
            f.setAmount("1");
            f.setType(FinanceType.CAPITAL);
            financeItems.add(f);
        }

        return financeItems;
    }

    private Node createPage(int pageIndex) {
        int pageSize = 10;
        int first = pageIndex * pageSize;
        int last = Math.min(first + pageSize, master.size());
        table.getItems().setAll(FXCollections.observableArrayList(master.subList(first, last)));

        return table;
    }


    public void onMenuEndSession(ActionEvent actionEvent) {

    }

    // Methods to add:

    // Method(s) that allow the user to switch between seeing only Assets,
    // Liabilities, and Capital investments as well as a view that allows them to
    // see all three
    // Should be in chronological order

    // Method that allows the user to click on the name of an item, and then view
    // all items of that type along with their asset/liability values and the dates
    // they were put in and their expiration date


}
