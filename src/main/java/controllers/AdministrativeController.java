package controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Employee;
import models.OrderedItem;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.Block;
import static com.mongodb.client.model.Filters.*;

public class AdministrativeController {

    MongoClient mc = new MongoClient();
	MongoDatabase database = mc.getDatabase("Restaurants");
	MongoCollection<Document> collection = database.getCollection("Employees");

    HashMap<Integer, Employee> empsCollection = new HashMap<Integer, Employee>();
    private Stage primaryStage;
    private Scene adminScene;
    private MainStageController mainController;

	void setPrimaryScene(Stage primaryStage, Scene adminScene, MainStageController mainStageController) {
		this.primaryStage = primaryStage;
		this.adminScene = adminScene;
		this.mainController = mainStageController;
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

    }

    public void login(int id, String password) {

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
		
		
		if(result.isPresent()) {
			AddEmployee(result.get());
		}

    }
    
    
    
    
    //Basic idea of what this should be

    Pagination myPagination = new Pagination();

    TableView table = new TableView();

    ObservableList<OrderedItem> master = FXCollections.observableArrayList();

    int pageSize = 50;

    InventoryTrackerController tracker = new InventoryTrackerController();


    public void init(){
        //Method to get all inventory items and set them to the ObservableList needed
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




	//Methods to add:

    //Method(s) that allow the user to switch between seeing only Assets, Liabilities, and Capital investments as well as a view that allows them to see all three
    //Should be in chronological order

    //Method that allows the user to click on the name of an item, and then view all items of that type along with their asset/liability values and the dates
    //they were put in and their expiration date
}
