package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import model.Meal;
import org.bson.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class POSController {

    private static double salesTax;
    public BorderPane borderPane;
    public MenuBar menuBar;
    public Pagination paginationPOS;
    private MongoClient mc = new MongoClient();
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Meals");
    private List<Meal> meals = new ArrayList<>();
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
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection dbCollection = db.getCollection("Meals");
    private TableView<Meal> mealTable = createTable();
    private ObservableList<Meal> data = fillMealCollection();
    private CurrentSession currentSession;

    public static double getSalesTax() {
        return salesTax;
    }

    public static void setSalesTax(double salesTax) {
        POSController.salesTax = salesTax;
    }

    void setPrimaryStage(Stage primaryStage, Scene posScene, MainStageController mainStageController, HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.primaryStage = primaryStage;
        this.scene = posScene;
        this.mainController = mainStageController;
        this.empsCollection = employeesCollection;
        this.currentSession = currentSession;
        this.primaryStage.setTitle("Inventory Tracker Manager - POS");

        if (data.size() > 100) {
            paginationPOS.setPageCount((data.size() / 100) + 1);
        } else {
            paginationPOS.setPageCount(1);
        }
        paginationPOS.setPageFactory(this::createPage);

        if (currentSession.isAdmin()) {
            System.out.println("you're an admin");
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

            Menu mealMenu = new Menu("Menu");
            MenuItem addMeal = new Menu("Add");
            MenuItem deleteMeal = new Menu("Delete");
            MenuItem updateMeal = new Menu("Update");
            mealMenu.getItems().add(addMeal);
            mealMenu.getItems().add(deleteMeal);
                mealMenu.getItems().add(updateMeal);

            menuBar.getMenus().add(mealMenu);

            addMeal.setOnAction(event -> {
                        Dialog<Meal> dialog = new Dialog<>();
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
                        TextField mealID = new TextField();
                        mealID.setPromptText("MealID");
                        TextField veganFriendly = new TextField();
                        veganFriendly.setPromptText("Vegan Friendly");
                        TextField totalCalorie = new TextField();
                        totalCalorie.setPromptText("totalCalorie");
                        TextField cost = new TextField();
                        cost.setPromptText("Cost");
//                        TextField numberOfIngredients = new TextField();
//                        numberOfIngredients.setPromptText("Number of Ingredients");

                        grid.add(new Label("Name:"), 0, 0);
                        grid.add(name, 1, 0);
                        grid.add(new Label("mealID:"), 0, 1);
                        grid.add(mealID, 1, 1);
                        grid.add(new Label("veganFriendly:"), 0, 2);
                        grid.add(veganFriendly, 1, 2);
                        grid.add(new Label("totalCalorie"), 0, 3);
                        grid.add(totalCalorie, 1, 3);
                        grid.add(new Label("cost"), 0, 4);
                        grid.add(cost, 1, 4);
//                        grid.add(new Label("BulkCost:"), 0, 5);
//                        grid.add(bulkCost, 1, 5);

                        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                        loginButton.setDisable(true);

                        name.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

                        dialog.getDialogPane().setContent(grid);

                        Platform.runLater(name::requestFocus);

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == loginButtonType) {
                                Meal m = new Meal();

                                m.setName(name.getText().trim());
                                m.setMealId(mealID.getText().trim());
                                m.setCost(cost.getText().trim());
                                m.setTotalCalorieCount(totalCalorie.getText().trim());
                                m.setVeganFriendly(veganFriendly.getText().trim());
//                                e.setBulkCost(bulkCost.getText().trim());

                                return m;
                            }
                            return null;
                        });
                Optional<Meal> result = dialog.showAndWait();

                result.ifPresent(meal -> addMeal(meal.getName(), Integer.parseInt(meal.getMealId()), Double.parseDouble(meal.getTotalCalorieCount()),
                        (meal.isVeganFriendly()), Double.parseDouble(meal.getCost())));
                    });

            deleteMeal.setOnAction(event -> {
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
                id.setPromptText("MealId");

                grid.add(new Label("mealID:"), 0, 1);
                grid.add(id, 1, 1);

                Node deleteButton = dialog.getDialogPane().lookupButton(deleteButtonType);
                deleteButton.setDisable(true);

                id.textProperty().addListener((observable, oldValue, newValue) -> deleteButton.setDisable(newValue.trim().isEmpty()));

                dialog.getDialogPane().setContent(grid);

                Platform.runLater(id::requestFocus);

                Meal e = new Meal();
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == deleteButtonType) {

                        e.setMealId(id.getText().trim());
//                    int ids = Integer.parseInt(e.getIngredientId());
                        delete(e.getMealId());
                    }
                    return null;
                });

                //TODO What are we doing with this?
                Optional<Ingredient> result = dialog.showAndWait();

                mealTable.getItems().removeAll();
                mealTable.refresh();
                data = null;
                data = fillMealCollection();
                mealTable.getItems().addAll(data);

            });

            pos.setSelected(true);
//            mealTable.setItems(fillMealCollection());
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
                admin.setSelected(true);
                adminController.setPrimaryStage(primaryStage, posScene, mainController, employeesCollection, currentSession);
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
                inventoryController.setPrimaryScene(primaryStage, scene, mainStageController, employeesCollection, currentSession);
                primaryStage.setMaxWidth(600);
                primaryStage.setMaxHeight(600);
                primaryStage.setScene(scene);
            });


        } else {

            System.out.println("not an admin");
        }

    }

    private void addMeal(String mealName, int mealID, double totalCalorieCount, String veganFriendly, double cost) {
        collection.insertOne(new Document("name", mealName).append("mealID", mealID)
                .append("totalCalorieCount", totalCalorieCount).append("veganFriendly", veganFriendly).append("cost", cost));
    }

    private void delete(String mealID) {
        collection.deleteOne(eq("mealID", mealID));
    }

    public void updateItem(int mealId, String updateField, String updateValue) {
        collection.updateOne(eq("mealID", mealId), new Document("$set", new Document(updateField, updateValue)));
    }


    private TableView<Meal> createTable() {

        mealTable = new TableView<>();
        mealTable.setEditable(true);

        TableColumn<Meal, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> mealID = new TableColumn<>("Meal ID");
        mealID.setCellValueFactory(new PropertyValueFactory<>("id"));
        mealID.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> cost = new TableColumn<>("Cost");
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        cost.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> veganFriendly = new TableColumn<>("Vegan Friendly");
        veganFriendly.setCellValueFactory(new PropertyValueFactory<>("veganFriendly"));
        veganFriendly.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> calorieCount = new TableColumn<>("Total Calorie Count");
        calorieCount.setCellValueFactory(new PropertyValueFactory<>("totalCalorie"));
        calorieCount.setCellFactory(TextFieldTableCell.forTableColumn());

//        TableColumn<Employee, String> occupation = new TableColumn<>("Ingredient List");
//        occupation.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
//        occupation.setCellFactory(TextFieldTableCell.forTableColumn());

        mealTable.getColumns().setAll(name, mealID, cost, veganFriendly, calorieCount);

        return mealTable;
    }

    private Node createPage(Integer pageIndex) {
        int rowsPerPage = 10;
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, (int) collection.countDocuments());

        for (int i = 0; i < mealTable.getItems().size(); i++) {
            System.out.println(mealTable.getItems().get(i));
        }
        mealTable.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex - 1)));
        return mealTable;
    }

    private ObservableList<Meal> fillMealCollection() {
        ObservableList<Meal> data = FXCollections.observableArrayList();
        List<DBObject> dbObjects = new ArrayList<>();

        for (int i = 0; i < collection.countDocuments(); i++) {

            DBObject query = BasicDBObjectBuilder.start().add("mealID", i + 1).get();
            DBCursor cursor = dbCollection.find(query);
            while (cursor.hasNext()) {
                dbObjects.add(cursor.next());
            }
        }

        Meal meal;
        for (int i = 1; i < collection.countDocuments(); i++) {
            meal = new Meal();
            meal.setName(dbObjects.get(i).get("name").toString());
            meal.setCost(dbObjects.get(i).get("cost").toString());
            meal.setMealId(dbObjects.get(i).get("mealID").toString());
//            meal.setTotalCalorieCount(dbObjects.get(i).get("totalCalorie").toString());
            meal.setVeganFriendly(dbObjects.get(i).get("veganFriendly").toString());
            data.add(meal);
        }
        return data;
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

    public void onMenuEndSession(ActionEvent actionEvent) {
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

    public void onMenuItemExit(ActionEvent actionEvent) {
        primaryStage.close();
    }


}
