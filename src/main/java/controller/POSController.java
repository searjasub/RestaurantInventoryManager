package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import model.Order;
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
    private MongoCollection<Document> orderCollection = database.getCollection("Orders");
    private MainStageController mainController;
    private HashMap<Integer, Employee> empsCollection;
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection dbCollection = db.getCollection("Meals");
    private DBCollection ordersDbCollection = db.getCollection("Orders");
    private TableView<Meal> mealTable = createTable();
    private ObservableList<Meal> data = fillMealCollection();
    private Scene scene;
    private CurrentSession currentSession;
    private List<Meal> meals = new ArrayList<>();
    private double totalCost;
    private double tip;
    private int orderNumber;
    private Stage primaryStage;

    void setPrimaryStage(Stage primaryStage, Scene posScene, MainStageController mainStageController, HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.primaryStage = primaryStage;
        this.scene = posScene;
        this.mainController = mainStageController;
        this.empsCollection = employeesCollection;
        this.currentSession = currentSession;
        this.primaryStage.setTitle("Inventory Tracker Manager - POS");

        if (data.size() > 10) {
            paginationPOS.setPageCount((data.size() / 10) + 1);
        } else {
            paginationPOS.setPageCount(1);
        }
        paginationPOS.setPageFactory(this::createPage);

        if (currentSession.isAdmin()) {
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

            Menu mealMenu = new Menu("Meal Menu");
            MenuItem addMeal = new Menu("Add");
            MenuItem deleteMeal = new Menu("Delete");
            MenuItem updateMeal = new Menu("Update");
            mealMenu.getItems().add(addMeal);
            mealMenu.getItems().add(deleteMeal);
            mealMenu.getItems().add(updateMeal);

            Menu orderMenu = new Menu("Order Menu");
            MenuItem startNewOrder = new Menu("Start New Order");
            MenuItem includeMeal = new Menu("Add Meal");
            MenuItem removeMeal = new Menu("Remove Meal");
            MenuItem splitOrder = new Menu("Split Order");
            MenuItem cashOut = new Menu("Cash Out");

            orderMenu.getItems().add(startNewOrder);
            orderMenu.getItems().add(includeMeal);
            orderMenu.getItems().add(removeMeal);
            orderMenu.getItems().add(splitOrder);
            orderMenu.getItems().add(cashOut);

            //TODO ADD MEAL
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
                ComboBox<String> veganFriendly = new ComboBox<>();
                veganFriendly.getItems().add("Yes");
                veganFriendly.getItems().add("No");
                TextField totalCalorie = new TextField();
                totalCalorie.setPromptText("Total Calories");
                TextField cost = new TextField();
                cost.setPromptText("Cost");
//                        TextField numberOfIngredients = new TextField();
//                        numberOfIngredients.setPromptText("Number of Ingredients");

                grid.add(new Label("Name"), 0, 0);
                grid.add(name, 1, 0);
                grid.add(new Label("Is vegan friendly?"), 0, 1);
                grid.add((veganFriendly), 1, 1);
                grid.add(new Label("Total calories"), 0, 2);
                grid.add(totalCalorie, 1, 2);
                grid.add(new Label("Cost"), 0, 3);
                grid.add(cost, 1, 3);
//                        grid.add(new Label("BulkCost:"), 0, 4);
//                        grid.add(bulkCost, 1, 4);

                Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                loginButton.setDisable(true);

                name.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

                dialog.getDialogPane().setContent(grid);

                Platform.runLater(name::requestFocus);

                String mealID = Long.toString(collection.countDocuments() + 1);
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == loginButtonType) {
                        Meal m = new Meal();
                        m.setName(name.getText().trim());
                        m.setMealID(mealID);
                        m.setCost(cost.getText().trim());
                        m.setTotalCalorieCount(totalCalorie.getText().trim());
                        m.setVeganFriendly(veganFriendly.getSelectionModel().getSelectedItem());
//                                e.setBulkCost(bulkCost.getText().trim());

                        return m;
                    }
                    return null;
                });
                Optional<Meal> result = dialog.showAndWait();

                result.ifPresent(meal -> addMeal(meal.getName(), Integer.parseInt(meal.getMealId()), Double.parseDouble(meal.getTotalCalorieCount()),
                        (meal.isVeganFriendly()), Double.parseDouble(meal.getCost())));

                mealTable.getItems().clear();
                mealTable.refresh();
                data = null;
                data = fillMealCollection();
                mealTable.getItems().addAll(data);
            });

            //TODO UPDATE MEAL
            updateMeal.setOnAction(event -> {
                Dialog<Meal> dialog = new Dialog<>();
                dialog.setTitle("Meal Dialog");
                dialog.setHeaderText("Please Input Meal Data To Update");

                ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField name = new TextField();
                name.setPromptText("Name");
                TextField mealID = new TextField();
                mealID.setPromptText("mealID");
                TextField totalCalorie = new TextField();
                totalCalorie.setPromptText("totalCalorie");
                ComboBox<String> veganFriendly = new ComboBox<>();
                veganFriendly.getItems().add("true");
                veganFriendly.getItems().add("false");
                TextField cost = new TextField();
                cost.setPromptText("cost");

                ObservableList<String> options =
                        FXCollections.observableArrayList(
                                "Name",
                                "Cost",
                                "Total Calorie Count",
                                "mealID",
                                "veganFriendly"
                        );
                final ComboBox comboBox = new ComboBox(options);

                grid.add(new Label("mealID:"), 0, 0);
                grid.add(mealID, 1, 0);
                grid.add(comboBox, 1, 1);

                comboBox.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
                    if (newValue.equals("Name")) {
                        grid.add(new Label("name"), 0, 1);
                        grid.add(name, 1, 1);
                        veganFriendly.setDisable(true);
                    }
                    if (newValue.equals("mealID")) {
                        grid.add(new Label("name"), 0, 1);
                        grid.add(name, 1, 1);
                        veganFriendly.setDisable(true);
                    }
                    if (newValue.equals("cost")) {
                        grid.add(new Label("cost"), 0, 1);
                        grid.add(cost, 1, 1);
                        veganFriendly.setDisable(true);
                    }
                    if (newValue.equals("totalCalorieCount")) {
                        grid.add(new Label("totalCalorieCount"), 0, 1);
                        grid.add(totalCalorie, 1, 1);
                        veganFriendly.setDisable(true);
                    }
                    if (newValue.equals("veganFriendly")) {
                        grid.getChildren().remove(comboBox);
                        veganFriendly.setDisable(false);
                        grid.add(new Label("veganFriendly"), 0, 1);
                        grid.add(veganFriendly, 1, 1);
                    }
//                        grid.getChildren().add(comboBox);
                });
                Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
                updateButton.setDisable(true);

                name.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
                cost.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
//                    veganFriendly.getSelectionModel().getSelectedItem().toString().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
                    totalCalorie.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
                    mealID.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));


                dialog.getDialogPane().setContent(grid);

                Platform.runLater(name::requestFocus);

                Meal e = new Meal();
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == updateButtonType) {

                            e.setName(name.getText().trim());
                            e.setMealID(mealID.getText());
                            e.setTotalCalorieCount(totalCalorie.getText().trim());
                            e.setCost(cost.getText().trim());
                            e.setVeganFriendly(veganFriendly.getSelectionModel().getSelectedItem());
                            System.out.println(e.getMealId());
                            int id = Integer.parseInt(e.getMealId());
                            if (!name.getText().isEmpty()) {
                                updateMeal(id, "name", e.getName());
                            }
                            if (!cost.getText().isEmpty()) {
                                updateMeal(id, "cost", e.getCost());
                            }
                            if (!totalCalorie.getText().isEmpty()) {
                                updateMeal(id, "totalCalorie", e.getTotalCalorieCount());
                            }
//                            if (!mealId.getText().isEmpty()) {
//                                updateMeal(id, "mealID", e.getMealId());
//                            }
                        System.out.println(e.isVeganFriendly());
                        if (!veganFriendly.isDisabled()) {

                            if (!veganFriendly.getSelectionModel().getSelectedItem().isEmpty()) {
                                updateMeal(id, "veganFriendly", e.isVeganFriendly());
                            }
                        }

                        return e;

                    }
                    return null;
                });
                Optional<Meal> result = dialog.showAndWait();

                    mealTable.getItems().clear();
                    mealTable.refresh();
                    data = null;
                    data = fillMealCollection();
                    mealTable.getItems().addAll(data);
                    mealTable.refresh();

                //TODO what's next?
                if (result.isPresent()) {
                    //deleteEmployee(result.get());
                }
            });

            //TODO DELETE MEAL
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
                id.setPromptText("mealID");

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

                        e.setMealID(id.getText().trim());
                        int ids = Integer.parseInt(e.getMealId());
                        deleteMeal(ids);
                    }
                    return null;
                });

                Optional<Ingredient> result = dialog.showAndWait();

                mealTable.getItems().clear();
                mealTable.refresh();
                data = null;
                data = fillMealCollection();
                mealTable.getItems().addAll(data);

            });

            includeMeal.setOnAction(event -> {
                Dialog<Meal> dialog = new Dialog<>();
                dialog.setTitle("Order Dialog");
                dialog.setHeaderText("Please Input Order Data");

                ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                ComboBox<String> orderSelection = new ComboBox<>();
                ComboBox<String> mealSelection = new ComboBox<>();

                for(Meal m : fillMealCollection()){
                    mealSelection.getItems().add(m.getMealId());
                }

                for(Order o : fillOrderCollection()){
                    orderSelection.getItems().add(o.getOrderID());
                }

                grid.add(new Label("Order Selection:"), 0, 0);
                grid.add(orderSelection, 1, 0);
                grid.add(new Label("Meal Selection:"), 0, 2);
                grid.add(( mealSelection), 1, 2);

                Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                loginButton.setDisable(false);

                dialog.getDialogPane().setContent(grid);
                Optional<Meal> result = dialog.showAndWait();
                String orderIdString = orderSelection.getSelectionModel().getSelectedItem();
                String mealIdString = mealSelection.getSelectionModel().getSelectedItem();
                List meals = fillMealCollection();
                double mealPrice = 0;
                for(Meal m : fillMealCollection()){
                    if(m.getMealId().equals(mealIdString)){
                        System.out.println(m.getMealId());
                        mealPrice = Double.parseDouble(m.getCost());
                        break;
                    }
                }
                for(Order o : fillOrderCollection()){
                    if(o.getOrderID().equals(orderIdString)){
                        o.addMealCost(Integer.parseInt(mealIdString),mealPrice);
                    }
                }

            });

            startNewOrder.setOnAction(event -> {
                Dialog<Meal> dialog = new Dialog<>();
                dialog.setTitle(" New Order Dialog");
                dialog.setHeaderText("Please Input Order Data");

                ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                ComboBox<String> mealSelection = new ComboBox<>();

                for(Meal m : fillMealCollection()){
                    mealSelection.getItems().add(m.getMealId());
                }

                long idTemp = orderCollection.countDocuments()+1;
                String idString = Long.toString(idTemp);
                System.out.println(idString);
                addOrder(Integer.parseInt(idString),0,0);



                grid.add(new Label("Meal Selection:"), 0, 2);
                grid.add(( mealSelection), 1, 2);

                Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                loginButton.setDisable(false);
                dialog.getDialogPane().setContent(grid);
//                Optional<Meal> result = dialog.showAndWait();
            });

            pos.setSelected(true);
            menuBar.getMenus().add(viewMenu);
            menuBar.getMenus().add(mealMenu);
            menuBar.getMenus().add(orderMenu);

            employees.setOnAction(event -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../EmployeesView.fxml"));
                BorderPane root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                EmployeesController employeesController = loader.getController();
                employees.setSelected(true);
                employeesController.setPrimaryStage(primaryStage, posScene, mainController, employeesCollection, currentSession);
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
                inventoryController.setPrimaryStage(primaryStage, scene, mainStageController, employeesCollection, currentSession);
                primaryStage.setMaxWidth(600);
                primaryStage.setMaxHeight(600);
                primaryStage.setScene(scene);
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

            finance.setOnAction(event -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../FinanceScene.fxml"));
                BorderPane root;
                Scene financeScene = null;
                try {
                    root = loader.load();
                    financeScene = new Scene(root, 600, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FinanceController financeController = loader.getController();

                financeController.setPrimaryStage(primaryStage, financeScene, mainStageController, employeesCollection, currentSession);
                primaryStage.setMaxWidth(600);
                primaryStage.setMaxHeight(600);
                primaryStage.setScene(financeScene);

            });

        } else {
            System.out.println("not an admin");
        }

    }

    private void addMeal(String mealName, int mealID, double totalCalorieCount, String veganFriendly, double cost) {
        boolean isVegan;
        isVegan = veganFriendly.equalsIgnoreCase("yes");
        collection.insertOne(new Document("name", mealName).append("mealID", mealID)
                .append("totalCalorie", totalCalorieCount).append("veganFriendly", isVegan).append("cost", cost));
    }
    private void addOrder( int orderID, double totalCalorieCount, double cost) {
        orderCollection.insertOne(new Document("orderID", orderID).append("totalCalorie", totalCalorieCount).append("cost", cost));
    }


    private TableView<Meal> createTable() {

        mealTable = new TableView<>();
        mealTable.setEditable(false);

        TableColumn<Meal, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> mealID = new TableColumn<>("mealID");
        mealID.setCellValueFactory(new PropertyValueFactory<>("id"));
        mealID.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> cost = new TableColumn<>("Cost");
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        cost.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> veganFriendly = new TableColumn<>("Vegan Friendly");
        veganFriendly.setCellValueFactory(new PropertyValueFactory<>("veganFriendly"));
        veganFriendly.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Meal, String> calorieCount = new TableColumn<>("Total Calorie Count");
        calorieCount.setCellValueFactory(new PropertyValueFactory<>("totalCalorieCount"));
        calorieCount.setCellFactory(TextFieldTableCell.forTableColumn());

        mealTable.getColumns().setAll(name, mealID, cost, veganFriendly, calorieCount);

        return mealTable;
    }

    private Node createPage(Integer pageIndex) {
        int rowsPerPage = 10;
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, (int) collection.countDocuments());
        mealTable.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return mealTable;
    }

    private ObservableList<Meal> fillMealCollection() {
        ObservableList<Meal> data = FXCollections.observableArrayList();
        List<DBObject> dbObjects;
        DBCursor cursor = dbCollection.find();
        dbObjects = cursor.toArray();
        Meal meal;
        for (DBObject obj : dbObjects) {
            meal = new Meal();
            meal.setName(obj.get("name").toString());
            meal.setTotalCalorieCount(obj.get("totalCalorie").toString());
            meal.setVeganFriendly(obj.get("veganFriendly").toString());
            meal.setMealID(obj.get("mealID").toString());
            meal.setCost(obj.get("cost").toString());
            data.add(meal);
        }
        return data;
    }

    private ObservableList<Order> fillOrderCollection() {
        ObservableList<Order> data = FXCollections.observableArrayList();
        List<DBObject> dbObjects;
        DBCursor cursor = ordersDbCollection.find();
        dbObjects = cursor.toArray();
        Order o;
        for (DBObject obj : dbObjects) {
            o = new Order();
            o.setOrderCost(obj.get("cost").toString());
            o.setOrderID(obj.get("orderID").toString());
            data.add(o);
        }
        return data;
    }

    public static double getSalesTax() {
        return salesTax;
    }

    public static void setSalesTax(double salesTax) {
        POSController.salesTax = salesTax;
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
