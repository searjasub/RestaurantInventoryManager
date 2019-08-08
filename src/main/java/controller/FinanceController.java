package controller;

import java.io.IOException;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Pagination;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.Employee;
import models.OrderedItem;

public class FinanceController {

	public MenuBar menuBar;
	private MongoClient mc = new MongoClient("localHost");
	private MongoDatabase database = mc.getDatabase("Restaurants");
	private MongoCollection<Document> collection = database.getCollection("Inventory");
	private Block<Document> printBlock = System.out::println;
	private Stage primaryStage;
	private AdministrativeController adminCon;
	private Scene financeScene;
	private MainStageController mainCon;
	private RadioMenuItem pos;
	private RadioMenuItem inventory;
	private RadioMenuItem admin;
	private Menu menu;
	
	TableView table = initTable();

	private Pagination myPagination;

	public void setPrimaryScene(Stage primaryStage, Scene administrativeScene, MainStageController mainStageController,
								RadioMenuItem admin, RadioMenuItem inventory, RadioMenuItem pos,
								HashMap<Integer, Employee> employeesCollection) {
		this.primaryStage = primaryStage;
		this.financeScene = administrativeScene;
		this.mainCon = mainStageController;
		this.admin = admin;
		this.inventory = inventory;
		this.pos = pos;
		this.menu = menu;
		this.menuBar = menuBar;

		this.menuBar.getMenus().add(menu);
		this.primaryStage.setTitle("Restaurant Inventory Manager - Finance");
		
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
            adminController.setPrimaryStage(primaryStage, administrativeScene, mainCon, employeesCollection, admin);
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
                posController.setPrimaryStage(primaryStage, posScene, mainCon, admin, inventory, pos, employeesCollection);
                primaryStage.setMaxWidth(600);
                primaryStage.setMaxHeight(600);
                primaryStage.setScene(posScene);

        });

	}


	private TableView initTable() {
		table = new TableView();
		table.setEditable(true);
		
		TableColumn id = new TableColumn();
		id.setText("ID");
		
		TableColumn name = new TableColumn();
		
		
		return table;
	}


	ObservableList<OrderedItem> master = FXCollections.observableArrayList();

	int pageSize = 50;

	InventoryTrackerController tracker = new InventoryTrackerController();

	public void init() {
		// Method to get all inventory items and set them to the ObservableList needed
		master = tracker.reviewOrderedItems();

		myPagination.setPageFactory(this::createPage);
	}

	@SuppressWarnings("unchecked")
	public Node createPage(int pageIndex) {
		int first = pageIndex * pageSize;
		int last = Math.min(first + pageSize, master.size());
		table.setItems(FXCollections.observableArrayList(master.subList(first, last)));

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

}