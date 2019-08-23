package controller;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
    private Stage primaryStage;
    private AdministrativeController adminCon;
    private Scene financeScene;
    private MainStageController mainStageController;
    private Pagination myPagination;
    private CurrentSession currentSession;

    void setPrimaryScene(Stage primaryStage, Scene administrativeScene, MainStageController mainStageController,
                         HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.primaryStage = primaryStage;
        this.financeScene = administrativeScene;
        this.mainStageController = mainStageController;
        this.currentSession = currentSession;
        this.primaryStage.setTitle("Restaurant Inventory Manager - Finance");

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
            inventory.setSelected(true);
            adminController.setPrimaryStage(primaryStage, administrativeScene, mainStageController, employeesCollection, currentSession);
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
        private int id;
        private String name;
        private int amount;
        private financeType type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
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
