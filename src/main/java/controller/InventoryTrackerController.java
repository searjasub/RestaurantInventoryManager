package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
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

@SuppressWarnings("DuplicatedCode")
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
    private CurrentSession currentSession;

    void setPrimaryScene(Stage primaryStage, Scene inventoryScene, MainStageController mainController, HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.primaryStage = primaryStage;
        this.inventoryScene = inventoryScene;
        this.mainController = mainController;
        this.currentSession = currentSession;
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

        menu.getMenus().add(viewMenu);

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(admin);
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
            TextField ingredientId = new TextField();
            ingredientId.setPromptText("IngredientID");
            TextField amount = new TextField();
            amount.setPromptText("Amount");
            TextField caloriePerServing = new TextField();
            caloriePerServing.setPromptText("CaloriePerServing");
            TextField costPerIngredient = new TextField();
            costPerIngredient.setPromptText("CostPerIngredient");
            TextField bulkCost = new TextField();
            bulkCost.setPromptText("BulkCost");

            grid.add(new Label("Name:"), 0, 0);
            grid.add(name, 1, 0);
            grid.add(new Label("IngredientId:"), 0, 1);
            grid.add(ingredientId, 1, 1);
            grid.add(new Label("Amount:"), 0, 2);
            grid.add(amount, 1, 2);
            grid.add(new Label("CaloriePerServing"), 0, 3);
            grid.add(caloriePerServing, 1, 3);
            grid.add(new Label("CostPerIngredient"), 0, 4);
            grid.add(costPerIngredient, 1, 4);
            grid.add(new Label("BulkCost:"), 0, 5);
            grid.add(bulkCost, 1, 5);

            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            name.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(name::requestFocus);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    Ingredient e = new Ingredient();

                    e.setName(name.getText().trim());
                    e.setIngredientId(ingredientId.getText().trim());
                    e.setAmount(amount.getText().trim());
                    e.setCaloriePerServing(caloriePerServing.getText().trim());
                    e.setCostPerIngredient(costPerIngredient.getText().trim());
                    e.setBulkCost(bulkCost.getText().trim());

                    return e;
                }
                return null;
            });

            Optional<Ingredient> result = dialog.showAndWait();

            result.ifPresent(ingredient -> addIngredient(ingredient.getName(), Integer.parseInt(ingredient.getIngredientId()), Integer.parseInt(ingredient.getCaloriePerServing()),
                    Integer.parseInt(ingredient.getAmount()), Integer.parseInt(ingredient.getCostPerIngredient()), Integer.parseInt(ingredient.getBulkCost())));
        });

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
//                    int ids = Integer.parseInt(e.getIngredientId());
                    deleteIngredient(e.getIngredientId());
                }
                return null;
            });

            //TODO What are we doing with this?
            Optional<Ingredient> result = dialog.showAndWait();

            ingredientTable.getItems().removeAll();
            ingredientTable.refresh();
            data = null;
            data = fillIngredientCollection();
            ingredientTable.getItems().addAll(data);

        });

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
            adminController.setPrimaryStage(primaryStage, inventoryScene, mainController, employeesCollection, currentSession);
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
            posController.setPrimaryStage(primaryStage, posScene, mainController, employeesCollection, currentSession);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(posScene);

        });
    }

    //Added this method to deal with the change over to StringProperty values instead of ints and dates. Will try to implement those soon
    private void addIngredient(String ingredientName, int ingredientId, int caloriePerServing, int amount, int costPerIngredient, int bulkCost) {
        collection.insertOne(new Document("ingredientName", ingredientName).append("ingredientID", ingredientId)
                .append("caloriePerServing", caloriePerServing).append("amount", amount).append("costPerIngredient", costPerIngredient)
                .append("bulkCost", bulkCost));
    }

    public void addItem(String ingredientName, int itemId, Date prepDate, Date expDate, int caloriePerServing,
                        int amount, int individualCost, int bulkCost, int bulkAmount) {
        collection.insertOne(new Document("ingredientName", ingredientName).append("ingredientID", itemId).append("prepDate", prepDate)
                .append("expDate", expDate).append("caloriePerServing", caloriePerServing).append("amount", amount).append("individualCost", individualCost)
                .append("bulkCost", bulkCost).append("bulkAmount", bulkAmount));
    }

    private void deleteIngredient(String ingredientId) {
        collection.deleteOne(eq("ingredientId", ingredientId));
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

    public void onMenuItemExit() {

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
//
//        TableColumn<Ingredient, String> expiredDate = new TableColumn<>("Expired Date");
//
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

        List<DBObject> dbObjects = new ArrayList<>();

        int id = 500001;
        for (int i = 0; i < collection.countDocuments() - 1; i++) {
            DBObject query = BasicDBObjectBuilder.start().add("ingredientID", id + i).get();
            DBCursor cursor = dbCollection.find(query);
            while (cursor.hasNext()) {
                dbObjects.add(cursor.next());
            }
        }


        Ingredient ingredient;
        for (int i = 0; i < collection.countDocuments() - 1; i++) {

            ingredient = new Ingredient();
            ingredient.setName(dbObjects.get(i).get("name").toString());
            ingredient.setIngredientId(dbObjects.get(i).get("ingredientID").toString());
            ingredient.setBulkCost(dbObjects.get(i).get("bulkCost").toString());
            ingredient.setCostPerIngredient(dbObjects.get(i).get("individualCost").toString());
            ingredient.setCaloriePerServing(dbObjects.get(i).get("caloriePerServing").toString());
            ingredient.setAmount(dbObjects.get(i).get("amount").toString());

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
