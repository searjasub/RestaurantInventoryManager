package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Employee;
import model.Ingredient;
import model.OrderedItem;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

@SuppressWarnings("DuplicatedCode")
public class InventoryTrackerController {

    public MenuBar menu;
    public Pagination inventoryPagination;
    private MongoClient mc = new MongoClient("localHost");
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Inventory");
    private Block<Document> printBlock = System.out::println;
    private EmployeesController controller;
    private Stage primaryStage;
    private Scene inventoryScene;
    private MainStageController mainStageController;
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection dbCollection = db.getCollection("Inventory");
    private ObservableList<Ingredient> data = fillIngredientCollection();
    private TableView<Ingredient> ingredientTable = createTable();
    private CurrentSession currentSession;

    void setPrimaryStage(Stage primaryStage, Scene inventoryScene, MainStageController mainStageController, HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.primaryStage = primaryStage;
        this.inventoryScene = inventoryScene;
        this.mainStageController = mainStageController;
        this.currentSession = currentSession;
        primaryStage.setTitle("Restaurant Inventory Manager - Inventory Tracker");

        if (data.size() > 10) {
            inventoryPagination.setPageCount((data.size() / 10) + 1);
        } else {
            inventoryPagination.setPageCount(1);
        }
        inventoryPagination.setPageFactory(this::createPage);


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

        menu.getMenus().add(viewMenu);

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(administrators);
        toggleGroup.getToggles().add(employees);
        toggleGroup.getToggles().add(inventory);
        toggleGroup.getToggles().add(pos);
        toggleGroup.getToggles().add(finance);

        inventory.setSelected(true);

        Menu ingredientsMenu = new Menu("Ingredients");
        MenuItem addIngredient = new Menu("Add");
        MenuItem deleteIngredient = new Menu("Delete");
        MenuItem updateIngredient = new Menu("Update");
        ingredientsMenu.getItems().add(addIngredient);
        ingredientsMenu.getItems().add(deleteIngredient);
        ingredientsMenu.getItems().add(updateIngredient);

        menu.getMenus().add(ingredientsMenu);

        //TODO ADD
        addIngredient.setOnAction(event -> {
            Dialog<Ingredient> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input Ingredient Data");

            ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField name = new TextField();
            name.setPromptText("Name");

            TextField amount = new TextField();
            amount.setPromptText("Amount");
            TextField caloriePerServing = new TextField();
            caloriePerServing.setPromptText("CaloriePerServing");
            TextField costPerIngredient = new TextField();
            costPerIngredient.setPromptText("CostPerIngredient");
            TextField bulkCost = new TextField();
            bulkCost.setPromptText("BulkCost");
            TextField wholesale = new TextField();

            grid.add(new Label("Name:"), 0, 0);
            grid.add(name, 1, 0);
            grid.add(new Label("Amount:"), 0, 1);
            grid.add(amount, 1, 1);
            grid.add(new Label("CaloriePerServing"), 0, 2);
            grid.add(caloriePerServing, 1, 2);
            grid.add(new Label("CostPerIngredient"), 0, 3);
            grid.add(costPerIngredient, 1, 3);
            grid.add(new Label("BulkCost:"), 0, 4);
            grid.add(bulkCost, 1, 4);
            grid.add(new Label("Wholesale:"), 0, 5);
            grid.add(bulkCost, 1, 5);


            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            name.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(name::requestFocus);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    Ingredient e = new Ingredient();
                    long id = 500000 + (collection.countDocuments() + 1);

                    e.setName(name.getText().trim());
                    e.setIngredientId("" + id);
                    e.setAmount(amount.getText().trim());
                    e.setCaloriePerServing(caloriePerServing.getText().trim());
                    e.setCostPerIngredient(costPerIngredient.getText().trim());
                    e.setBulkCost(bulkCost.getText().trim());
                    e.setWholesale(wholesale.getText().trim());

                    return e;

                }
                return null;
            });
            Optional<Ingredient> result = dialog.showAndWait();

            result.ifPresent(this::addIngredient);

            ingredientTable.getItems().clear();
            ingredientTable.refresh();
            data = null;
            data = fillIngredientCollection();
            ingredientTable.getItems().addAll(data);
        });

        //TODO DELETE
        deleteIngredient.setOnAction(event -> {
            Dialog<Ingredient> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input Ingredient Data To Delete");

            ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField id = new TextField();
            id.setPromptText("IngredientID");

            grid.add(new Label("IngredientID:"), 0, 1);
            grid.add(id, 1, 1);

            Node deleteButton = dialog.getDialogPane().lookupButton(deleteButtonType);
            deleteButton.setDisable(true);

            id.textProperty().addListener((observable, oldValue, newValue) -> deleteButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(id::requestFocus);

            Ingredient e = new Ingredient();
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == deleteButtonType) {

                    e.setIngredientId(id.getText().trim());
                    int ids = Integer.parseInt(e.getIngredientId());
                    deleteIngredient(ids);
                }
                return null;
            });

            Optional<Ingredient> result = dialog.showAndWait();

            ingredientTable.getItems().clear();
            ingredientTable.refresh();
            data = null;
            data = fillIngredientCollection();
            ingredientTable.getItems().addAll(data);

        });

        //TODO UPDATE
        updateIngredient.setOnAction(event -> {
            Dialog<Ingredient> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input Employee Data To Update");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField name = new TextField();
            name.setPromptText("name");
            TextField id = new TextField();
            id.setPromptText("ingredientID");
            TextField caloriePerServing = new TextField();
            caloriePerServing.setPromptText("caloriePerServing");
            TextField amount = new TextField();
            amount.setPromptText("amount");
            TextField individualCost = new TextField();
            individualCost.setPromptText("individualCost");
            TextField bulkCost = new TextField();
            bulkCost.setPromptText("bulkCost");

            ObservableList<String> options =
                    FXCollections.observableArrayList(
                            "Name",
                            "ingredientID",
                            "caloriePerServings",
                            "amount",
                            "individualCost",
                            "bulkCost"
                    );
            final ComboBox comboBox = new ComboBox(options);

            grid.add(new Label("ingredientID:"), 0, 0);
            grid.add(id, 1, 0);
            grid.add(comboBox, 1, 1);

            comboBox.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
                if (newValue.equals("name")) {
                    grid.add(new Label("name"), 0, 1);
                    grid.add(name, 1, 1);
                }
                if (newValue.equals("ingredientID")) {
                    grid.add(new Label("IngredientID"), 0, 1);
                    grid.add(id, 1, 1);
                }
                if (newValue.equals("caloriePerServings")) {
                    grid.add(new Label("caloriePerServings"), 0, 1);
                    grid.add(caloriePerServing, 1, 1);
                }
                if (newValue.equals("amount")) {
                    grid.add(new Label("amount"), 0, 1);
                    grid.add(amount, 1, 1);
                }
                if (newValue.equals("individualCost")) {
                    grid.add(new Label("individualCost"), 0, 1);
                    grid.add(individualCost, 1, 1);
                }
                if (newValue.equals("bulkCost")) {
                    grid.add(new Label("bulkCost"), 0, 1);
                    grid.add(bulkCost, 1, 1);
                }
            });

            Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
            updateButton.setDisable(true);

            name.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            amount.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            caloriePerServing.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            individualCost.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            bulkCost.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            id.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);
            Platform.runLater(name::requestFocus);
            Ingredient i = new Ingredient();
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {

                    i.setName(name.getText().trim());
                    i.setIngredientId(id.getText().trim());
                    int ids = Integer.parseInt(i.getIngredientId());
                    i.setCaloriePerServing(caloriePerServing.getText().trim());
                    i.setAmount(amount.getText().trim());
                    i.setCostPerIngredient(individualCost.getText().trim());
                    i.setBulkCost(bulkCost.getText().trim());

                    if (!name.getText().isEmpty()) {
                        updateItem(ids, "name", i.getName());
                    }
                    if (!id.getText().isEmpty()) {
                        updateItem(ids, "ID", i.getIngredientId());
                    }
                    if (!caloriePerServing.getText().isEmpty()) {
                        updateItem(ids, "caloriePerServing", i.getCaloriePerServing());
                    }
                    if (!amount.getText().isEmpty()) {
                        updateItem(ids, "amount", i.getAmount());
                    }
                    if (!individualCost.getText().isEmpty()) {
                        updateItem(ids, "individualCost", i.getCostPerIngredient());
                    }
                    if (!bulkCost.getText().isEmpty()) {
                        updateItem(ids, "bulkCost", i.getBulkCost());
                    }

                    return i;
                }
                return null;
            });

            Optional<Ingredient> result = dialog.showAndWait();

            ingredientTable.getItems().clear();
            ingredientTable.refresh();
            data = null;
            data = fillIngredientCollection();
            ingredientTable.getItems().addAll(data);

            //TODO what's next?
            if (result.isPresent()) {
                //deleteEmployee(result.get());
            }
        });

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
            EmployeesController employeesController = loader.getController();
            inventory.setSelected(true);
            employeesController.setPrimaryStage(primaryStage, inventoryScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMaxWidth(800);
            primaryStage.setMaxHeight(800);
            primaryStage.setScene(new Scene(Objects.requireNonNull(root), 600, 600));
        });

        pos.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../POSScene.fxml"));
            Scene posScene = null;
            BorderPane root;
            try {
                root = loader.load();
                posScene = new Scene(root, 800, 800);
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

    private void addIngredient(Ingredient ingredient) {
        try {
            if (ingredient.getName().matches("^([0-9])*$")){
                throw new IllegalArgumentException();
            }
            if (ingredient.getName().isEmpty() || ingredient.getIngredientId().isEmpty() || ingredient.getCostPerIngredient().isEmpty() || ingredient.getAmount().isEmpty() || ingredient.getCaloriePerServing().isEmpty() || ingredient.getBulkCost().isEmpty()){
                throw new NullPointerException();
            }
            collection.insertOne(new Document("name", ingredient.getName()).append("ingredientID", Integer.parseInt(ingredient.getIngredientId()))
                    .append("caloriePerServing", Integer.parseInt(ingredient.getCaloriePerServing())).append("amount", Integer.parseInt(ingredient.getAmount())).append("individualCost", Integer.parseInt(ingredient.getCostPerIngredient())).append("bulkCost", Integer.parseInt(ingredient.getBulkCost())).append("wholesale", Integer.parseInt(ingredient.getWholesale())));
            ingredientTable.getItems().clear();
            data = null;
            data = fillIngredientCollection();
            ingredientTable.getItems().addAll(data);
        } catch (NumberFormatException ex){
            showAlertFillInfo();
        } catch (IllegalArgumentException ex){
            showAlertNumericName();
        }
    }

    private void showAlertNumericName() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Name of ingredient cannot be only numbers", ButtonType.OK);
        alert.setTitle("Name not valid");
        alert.show();
    }

    private void showAlertFillInfo() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Complete all fields", ButtonType.OK);
        alert.setTitle("Ingredient not completed");
        alert.show();
    }

    //Added this method to deal with the change over to StringProperty values instead of ints and dates. Will try to implement those soon
    private void addIngredient(String ingredientName, int ingredientId, int caloriePerServing, int amount, int costPerIngredient, int bulkCost) {
        try {
            collection.insertOne(new Document("name", ingredientName).append("ingredientID", ingredientId)
                    .append("caloriePerServing", caloriePerServing).append("amount", amount).append("individualCost", costPerIngredient)
                    .append("bulkCost", bulkCost));
        } catch (NumberFormatException ex){
            showAlertFillInfo();
        }
    }

    public void addItem(String ingredientName, int itemId, Date prepDate, Date expDate, int caloriePerServing,
                        int amount, int individualCost, int bulkCost, int bulkAmount) {
        collection.insertOne(new Document("ingredientName", ingredientName).append("ingredientID", itemId).append("prepDate", prepDate)
                .append("expDate", expDate).append("caloriePerServing", caloriePerServing).append("amount", amount).append("individualCost", individualCost)
                .append("bulkCost", bulkCost).append("bulkAmount", bulkAmount));
    }

    private void deleteIngredient(int ingredientId) {
        collection.deleteOne(eq("ingredientID", ingredientId));
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

    private TableView<Ingredient> createTable() {

        ingredientTable = new TableView<>();
        ingredientTable.setEditable(true);

        TableColumn<Ingredient, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(event -> event.getTableView().getItems()
                .get(event.getTablePosition().getRow()).setName(event.getNewValue()));

        TableColumn<Ingredient, String> ingredientId = new TableColumn<>("Ingredient ID");
        ingredientId.setCellValueFactory(new PropertyValueFactory<>("ingredientId"));
        ingredientId.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Ingredient, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amount.setCellFactory(TextFieldTableCell.forTableColumn());
        amount.setOnEditCommit(event -> event.getTableView().getItems()
                .get(event.getTablePosition().getRow()).setAmount(event.getNewValue()));

//        TableColumn<Ingredient, String> prepDate = new TableColumn<>("Prep Date");
//        TableColumn<Ingredient, String> expiredDate = new TableColumn<>("Expired Date");
//        TableColumn<Ingredient, String> veganFriendly = new TableColumn<>("Vegan Friendly");

        TableColumn<Ingredient, String> caloriePerServing = new TableColumn<>("Calories Per Serving");
        caloriePerServing.setCellValueFactory(new PropertyValueFactory<>("caloriePerServing"));
        caloriePerServing.setCellFactory(TextFieldTableCell.forTableColumn());
        caloriePerServing.setOnEditCommit(event -> event.getTableView().getItems()
                .get(event.getTablePosition().getRow()).setCaloriePerServing(event.getNewValue()));

        TableColumn<Ingredient, String> costPerIngredient = new TableColumn<>("Cost Per Ingredient");
        costPerIngredient.setCellValueFactory(new PropertyValueFactory<>("costPerIngredient"));
        costPerIngredient.setCellFactory(TextFieldTableCell.forTableColumn());
        costPerIngredient.setOnEditCommit(event -> event.getTableView().getItems()
                .get(event.getTablePosition().getRow()).setCostPerIngredient(event.getNewValue()));

        TableColumn<Ingredient, String> bulkCost = new TableColumn<>("Bulk Cost");
        bulkCost.setCellValueFactory(new PropertyValueFactory<>("bulkCost"));
        bulkCost.setCellFactory(TextFieldTableCell.forTableColumn());
        bulkCost.setOnEditCommit(event -> event.getTableView().getItems()
                .get(event.getTablePosition().getRow()).setBulkCost(event.getNewValue()));
        ingredientTable.getColumns().setAll(name, ingredientId, amount,/* prepDate, expiredDate, veganFriendly,*/ caloriePerServing, costPerIngredient, bulkCost);

        return ingredientTable;

    }

    private Node createPage(Integer pageIndex) {
        int rowsPerPage = 10;
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, (int) collection.countDocuments());
        ingredientTable.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return ingredientTable;
    }

    private ObservableList<Ingredient> fillIngredientCollection() {
        ObservableList<Ingredient> data = FXCollections.observableArrayList();

        List<DBObject> dbObjects;

        DBCursor cursor = dbCollection.find();
        dbObjects = cursor.toArray();
        Ingredient ingredient;
        for (DBObject obj : dbObjects) {
            ingredient = new Ingredient();
            ingredient.setName(obj.get("name").toString());
            ingredient.setIngredientId(obj.get("ingredientID").toString());
            ingredient.setBulkCost(obj.get("bulkCost").toString());
            ingredient.setCostPerIngredient(obj.get("individualCost").toString());
            ingredient.setCaloriePerServing(obj.get("caloriePerServing").toString());
            ingredient.setAmount(obj.get("amount").toString());

            data.add(ingredient);

        }
        return data;
    }

    public void onMenuEndSession() {
        currentSession.restartSession();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/MainStage.fxml"));
            BorderPane root = loader.load();
            MainStageController c = loader.getController();
            Scene scene = new Scene(root, 400, 400);
            c.setPrimaryStage(primaryStage, scene);
            primaryStage.setTitle("Restaurant Inventory Manager");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(530);
            primaryStage.setMinHeight(250);
            primaryStage.setMaxHeight(250);
            primaryStage.setMaxWidth(530);
            primaryStage.show();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
