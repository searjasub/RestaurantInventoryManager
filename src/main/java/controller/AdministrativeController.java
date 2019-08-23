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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Employee;
import org.bson.Document;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

@SuppressWarnings("unused")
public class AdministrativeController {

    public MenuBar menuBar = new MenuBar();
    public Pagination pagination;
    private MongoClient mc = new MongoClient();
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Employees");
    private HashMap<Integer, Employee> employeeHashMap;
    private Stage primaryStage;
    private Scene adminScene;
    private MainStageController mainController;
    private TableView<Employee> empsTable = createTable();
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection dbCollection = db.getCollection("Employees");
    private ObservableList<Employee> data = fillEmpCollection();
    private CurrentSession currentSession;

    void setPrimaryStage(Stage primaryStage, Scene adminScene, MainStageController mainStageController, HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.primaryStage = primaryStage;
        this.adminScene = adminScene;
        this.mainController = mainStageController;
        this.employeeHashMap = employeesCollection;
        this.currentSession = currentSession;
        primaryStage.setTitle("Restaurant Inventory Manager - Administrator");


        if (data.size() > 100) {
            pagination.setPageCount((data.size() / 100) + 1);
        } else {
            pagination.setPageCount(1);
        }
        pagination.setPageFactory(this::createPage);

        Menu viewMenu = new Menu("View");
        RadioMenuItem admin = new RadioMenuItem("Admin");
        RadioMenuItem inventory = new RadioMenuItem("Inventory");
        RadioMenuItem pos = new RadioMenuItem("POS");
//        RadioMenuItem finance = new RadioMenuItem("Finance");

        viewMenu.getItems().add(admin);
        viewMenu.getItems().add(inventory);
        viewMenu.getItems().add(pos);
//        viewMenu.getItems().add(finance);

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(admin);
        toggleGroup.getToggles().add(inventory);
        toggleGroup.getToggles().add(pos);
//        toggleGroup.getToggles().add(finance);
        admin.setSelected(true);

        Menu employeesMenu = new Menu("Employees");
        MenuItem addEmployee = new Menu("Add");
        MenuItem deleteEmployee = new Menu("Delete");
        MenuItem updateEmployee = new Menu("Update");
        employeesMenu.getItems().add(addEmployee);
        employeesMenu.getItems().add(deleteEmployee);
        employeesMenu.getItems().add(updateEmployee);


        JTree jTree = new JTree();
        jTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_N) {

                }
            }

        });

        //TODO ADD EMPLOYEE
        addEmployee.setOnAction(event -> {
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
            TextField weeklyHours = new TextField();
            weeklyHours.setPromptText("WeeklyHours");
            TextField password = new TextField();
            password.setPromptText("Password");
            TextField hourlyPay = new TextField();
            hourlyPay.setPromptText("HourlyPay");
            TextField occupation = new TextField();
            occupation.setPromptText("Occupation");

            grid.add(new Label("Name:"), 0, 0);
            grid.add(name, 1, 0);
            grid.add(new Label("WeeklyHours"), 0, 1);
            grid.add(weeklyHours, 1, 1);
            grid.add(new Label("Password"), 0, 2);
            grid.add(password, 1, 2);
            grid.add(new Label("HourlyPay"), 0, 3);
            grid.add(hourlyPay, 1, 3);
            grid.add(new Label("Occupation:"), 0, 4);
            grid.add(occupation, 1, 4);

            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);


            name.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
            weeklyHours.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
            password.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
            hourlyPay.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
            occupation.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);
            Platform.runLater(name::requestFocus);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    Employee e = new Employee();

                    e.setName(name.getText().trim());
                    e.setId("" + (collection.countDocuments() + 1));
                    e.setWeeklyHours(weeklyHours.getText().trim());
                    e.setPassword(password.getText().trim());
                    e.setHourlyPay(hourlyPay.getText().trim());
                    e.setOccupation(occupation.getText().trim());
                    return e;
                }
                return null;
            });

            Optional<Employee> result = dialog.showAndWait();

            empsTable.getItems().clear();
            empsTable.refresh();
            data = null;
            data = fillEmpCollection();
            empsTable.getItems().addAll(data);
            empsTable.refresh();

            result.ifPresent(this::AddEmployee);
        });

        //TODO DELETE EMPLOYEE
        deleteEmployee.setOnAction(event -> {
            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input Employee Data To Delete");

            ButtonType deleteButtonType = new ButtonType("Delete", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField id = new TextField();
            id.setPromptText("EmployeeID");

            grid.add(new Label("EmployeeID:"), 0, 1);
            grid.add(id, 1, 1);

            Node deleteButton = dialog.getDialogPane().lookupButton(deleteButtonType);
            deleteButton.setDisable(true);

            id.textProperty().addListener((observable, oldValue, newValue) -> deleteButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(id::requestFocus);

            Employee e = new Employee();
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == deleteButtonType) {

                    e.setId(id.getText().trim());
                    int ids = Integer.parseInt(e.getId());
                    deleteEmployee(ids);
                }
                return null;
            });

            Optional<Employee> result = dialog.showAndWait();

            empsTable.getItems().clear();
            empsTable.refresh();
            data = null;
            data = fillEmpCollection();
            empsTable.getItems().addAll(data);
            empsTable.refresh();
        });

        //TODO UPDATE EMPLOYEE
        updateEmployee.setOnAction(event -> {
            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input Employee Data To Delete");

            ButtonType updateButtonType = new ButtonType("Update", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField name = new TextField();
            name.setPromptText("Name");
            TextField id = new TextField();
            id.setPromptText("EmployeeId");
            TextField weeklyHours = new TextField();
            weeklyHours.setPromptText("WeeklyHours");
            TextField password = new TextField();
            password.setPromptText("Password");
            TextField hourlyPay = new TextField();
            hourlyPay.setPromptText("HourlyPay");
            TextField occupation = new TextField();
            occupation.setPromptText("Occupation");

            ObservableList<String> options =
                    FXCollections.observableArrayList(
                            "Full Name",
                            "WeeklyHours",
                            "Password",
                            "HourlyPay",
                            "Occupation",
                            "EmployeeID"
                    );
            final ComboBox comboBox = new ComboBox(options);

            grid.add(new Label("EmployeeId:"), 0, 0);
            grid.add(id, 1, 0);
            grid.add(comboBox, 1, 1);

            comboBox.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
                if (newValue.equals("Full Name")) {
                    grid.add(new Label("Full Name"), 0, 1);
                    grid.add(name, 1, 1);
                }
                if (newValue.equals("WeeklyHours")) {
                    grid.add(new Label("WeeklyHours"), 0, 1);
                    grid.add(weeklyHours, 1, 1);
                }
                if (newValue.equals("Password")) {
                    grid.add(new Label("Password"), 0, 1);
                    grid.add(password, 1, 1);
                }
                if (newValue.equals("HourlyPay")) {
                    grid.add(new Label("HourlyPay"), 0, 1);
                    grid.add(hourlyPay, 1, 1);
                }
                if (newValue.equals("Occupation")) {
                    grid.add(new Label("Occupation"), 0, 1);
                    grid.add(occupation, 1, 1);
                }
                if (newValue.equals("employeeID")) {
                    grid.add(new Label("employeeID"), 0, 1);
                    grid.add(occupation, 1, 1);
                }
            });


            Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
            updateButton.setDisable(true);

            name.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            password.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            weeklyHours.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            hourlyPay.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            occupation.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));
            id.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);
            Platform.runLater(name::requestFocus);
            Employee e = new Employee();
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    e.setName(name.getText().trim());
                    e.setId(id.getText().trim());
                    int ids = Integer.parseInt(e.getId());
                    e.setPassword(password.getText().trim());
                    e.setWeeklyHours(weeklyHours.getText().trim());
                    e.setHourlyPay(hourlyPay.getText().trim());
                    e.setOccupation(occupation.getText().trim());

                    if (!name.getText().isEmpty()) {
                        updateEmployee(ids, "name", e.getName());
                    }
                    if (!password.getText().isEmpty()) {
                        updateEmployee(ids, "password", e.getPassword());
                    }
                    if (!weeklyHours.getText().isEmpty()) {
                        updateEmployee(ids, "weeklyHours", e.getWeeklyHours());
                    }
                    if (!hourlyPay.getText().isEmpty()) {
                        updateEmployee(ids, "hourlyPay", e.getHourlyPay());
                    }
                    if (!occupation.getText().isEmpty()) {
                        updateEmployee(ids, "occupation", e.getOccupation());
                    }
                    if (!id.getText().isEmpty()) {
                        updateEmployee(ids, "employeeID", e.getId());
                    }
                    return e;
                }
                return null;
            });

            Optional<Employee> result = dialog.showAndWait();

            empsTable.getItems().clear();
            empsTable.refresh();
            data = null;
            data = fillEmpCollection();
            empsTable.getItems().addAll(data);
            empsTable.refresh();

            //TODO what's next?
            if (result.isPresent()) {
                //deleteEmployee(result.get());
            }
        });

        menuBar.getMenus().add(viewMenu);
        menuBar.getMenus().add(employeesMenu);

        inventory.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../InventoryTrackerScene.fxml"));
            BorderPane root;
            Scene inventoryScene = null;
            try {
                root = loader.load();
                inventoryScene = new Scene(root, 600, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
            InventoryTrackerController inventoryController = loader.getController();

            inventoryController.setPrimaryScene(primaryStage, inventoryScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMinHeight(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(inventoryScene);
        });

//        finance.setOnAction(event -> {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FinanceScene.fxml"));
//            BorderPane root;
//            Scene financeScene = null;
//            try {
//                root = loader.load();
//                financeScene = new Scene(root, 600, 600);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            FinanceController financeController = loader.getController();
//
//            financeController.setPrimaryScene(primaryStage, financeScene, mainStageController, employeesCollection, currentSession);
//            primaryStage.setMaxWidth(600);
//            primaryStage.setMaxHeight(600);
//            primaryStage.setScene(financeScene);
//
//        });

        pos.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../POSScene.fxml"));
            BorderPane root;
            Scene posScene = null;
            try {
                root = loader.load();
                posScene = new Scene(root, 600, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
            POSController posController = loader.getController();
            posController.setPrimaryStage(primaryStage, posScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMaxWidth(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(posScene);
        });
    }

    private TableView<Employee> createTable() {
        empsTable = new TableView<>();
        empsTable.setEditable(true);

        TableColumn<Employee, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue()));

        TableColumn<Employee, String> employeeId = new TableColumn<>("Employee ID");
        employeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeId.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Employee, String> weeklyHours = new TableColumn<>("Weekly Hours");
        weeklyHours.setCellValueFactory(new PropertyValueFactory<>("weeklyHours"));
        weeklyHours.setCellFactory(TextFieldTableCell.forTableColumn());
        weeklyHours.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setWeeklyHours(event.getNewValue()));

        TableColumn<Employee, String> password = new TableColumn<>("Password");
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        password.setCellFactory(TextFieldTableCell.forTableColumn());
        password.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setPassword(event.getNewValue()));

        TableColumn<Employee, String> hourlyPay = new TableColumn<>("Hourly Pay");
        hourlyPay.setCellValueFactory(new PropertyValueFactory<>("hourlyPay"));
        hourlyPay.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Employee, String> occupation = new TableColumn<>("Occupation");
        occupation.setCellValueFactory(new PropertyValueFactory<>("occupation"));
        occupation.setCellFactory(TextFieldTableCell.forTableColumn());
        occupation.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setOccupation(event.getNewValue()));

//        TableColumn<Employee, String> clockIn = new TableColumn<>("Clock In");
//        TableColumn<Employee, String> clockOut = new TableColumn<>("Clock Out");
//        TableColumn<Employee, String> breakStart = new TableColumn<>("Break Start");
//        TableColumn<Employee, String> breakEnd = new TableColumn<>("Break End");

        empsTable.getColumns().setAll(name, employeeId, weeklyHours, password, occupation, hourlyPay);

        return empsTable;

    }

    private Node createPage(Integer pageIndex) {
        int rowsPerPage = 10;
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, (int) collection.countDocuments());

        empsTable.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return empsTable;
    }

    private void showAlertFillInfo() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Complete all fields", ButtonType.OK);
        alert.setTitle("Employee not completed");
        alert.show();
    }

    private void AddEmployee(Employee e) {
        try {
            collection.insertOne(new Document("name", e.getName()).append("weeklyHours", Integer.parseInt(e.getWeeklyHours())).append("employeeID", Integer.parseInt(e.getId()) + 100000).append("password", e.getPassword())
                    .append("hourlyPay", Integer.parseInt(e.getHourlyPay())).append("occupation", e.getOccupation()));
            empsTable.getItems().clear();
            data = null;
            data = fillEmpCollection();
            empsTable.getItems().addAll(data);
        } catch (NumberFormatException ex){
            showAlertFillInfo();
        }
    }

    private void deleteEmployee(int e) {
        collection.deleteOne(eq("employeeID", e));
    }

    public void updateEmployee(int id, String updateField, String updateValue) {
        collection.updateOne(eq("employeeID", id), new Document("$set", new Document(updateField, updateValue)));
    }

    public void startBreak(Date time, int id) {
        collection.updateOne(eq("employeeID", id), new Document("$set", new Document("breakStart", time)));
    }

    public void endBreak(Date time, int id) {
        collection.updateOne(eq("employeeID", id), new Document("$set", new Document("breakEnd", time)));
    }

    public void clockIn(int id, Date time) {
        collection.updateOne(eq("employeeID", id), new Document("$set", new Document("clockIn", time)));
    }

    public void clockOut(int id, Date time) {
        collection.updateOne(eq("employeeID", id), new Document("$set", new Document("clockOut", time)));
    }


    public void login(int id, String password) {
        BasicDBObject andQuery = new BasicDBObject();
        List<BasicDBObject> obj;
        obj = new ArrayList<>();

        obj.add(new BasicDBObject("employeeID", id));
        obj.add(new BasicDBObject("password", password));
        andQuery.put("$and", obj);
        collection.find(andQuery);
    }

    private ObservableList<Employee> fillEmpCollection() {
        ObservableList<Employee> data = FXCollections.observableArrayList();
        List<DBObject> dbObjects;
        DBCursor cursor = dbCollection.find();
        dbObjects = cursor.toArray();

        for (DBObject obj : dbObjects) {
            Employee employee = MainStageController.getEmployee(obj);
            data.add(employee);
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

    public void allShow() {

    }
}

