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
import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class AdminController {


    public Pagination pagination;
    public MenuBar menuBar;
    private TableView<Employee> adminTable = createTable();
    private Stage primaryStage;
    private MongoClient mc = new MongoClient();
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private MongoCollection<Document> collection = database.getCollection("Administrators");
    private HashMap<Integer, Employee> employeeHashMap;
    private Scene adminScene;
    private MainStageController mainStageController;
    private HashMap<Integer, Employee> employeesCollection;
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private DBCollection dbCollection = db.getCollection("Administrators");
    private ObservableList<Employee> data = fillAdminCollection();
    private CurrentSession currentSession;

    public void setPrimaryStage(Stage primaryStage, Scene adminScene, MainStageController mainStageController, HashMap<Integer, Employee> employeesCollection, CurrentSession currentSession) {
        this.currentSession = currentSession;
        this.primaryStage = primaryStage;
        this.adminScene = adminScene;
        this.mainStageController = mainStageController;
        this.employeesCollection = employeesCollection;

        primaryStage.setTitle("Restaurant Inventory Manager - Administrators");


        if (data.size() > 100) {
            pagination.setPageCount((data.size() / 100) + 1);
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

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(administrators);
        toggleGroup.getToggles().add(employees);
        toggleGroup.getToggles().add(inventory);
        toggleGroup.getToggles().add(pos);
        toggleGroup.getToggles().add(finance);
        administrators.setSelected(true);

        Menu employeesMenu = new Menu("Admins");
        MenuItem addEmployee = new Menu("Add");
        MenuItem deleteEmployee = new Menu("Delete");
        MenuItem updateEmployee = new Menu("Update");
        employeesMenu.getItems().add(addEmployee);
        employeesMenu.getItems().add(deleteEmployee);
        employeesMenu.getItems().add(updateEmployee);

        //TODO ADD EMPLOYEE
        addEmployee.setOnAction(event -> {
            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input Admin Data");

            ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
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

            adminTable.getItems().clear();
            adminTable.refresh();
            data = null;
            data = fillAdminCollection();
            adminTable.getItems().addAll(data);
            adminTable.refresh();

            result.ifPresent(this::addAdmin);
        });

        //TODO DELETE EMPLOYEE
        deleteEmployee.setOnAction(event -> {
            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input Admin Data To Delete");

            ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField id = new TextField();
            id.setPromptText("AdminID");

            grid.add(new Label("AdminID:"), 0, 1);
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
                    deleteAdmin(ids);
                }
                return null;
            });

            Optional<Employee> result = dialog.showAndWait();

            adminTable.getItems().clear();
            adminTable.refresh();
            data = null;
            data = fillAdminCollection();
            adminTable.getItems().addAll(data);
            adminTable.refresh();
        });

        //TODO UPDATE EMPLOYEE
        updateEmployee.setOnAction(event -> {
            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle("Contact Dialog");
            dialog.setHeaderText("Please Input admin Data To Delete");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField name = new TextField();
            name.setPromptText("Name");
            TextField id = new TextField();
            id.setPromptText("AdminID");
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
                            "AdminID"
                    );
            final ComboBox comboBox = new ComboBox(options);

            grid.add(new Label("AdminID:"), 0, 0);
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
                if (newValue.equals("AdminID")) {
                    grid.add(new Label("AdminID"), 0, 1);
                    grid.add(id, 1, 1);
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
                        updateAdmin(ids, "name", e.getName());
                    }
                    if (!password.getText().isEmpty()) {
                        updateAdmin(ids, "password", e.getPassword());
                    }
                    if (!weeklyHours.getText().isEmpty()) {
                        updateAdmin(ids, "weeklyHours", e.getWeeklyHours());
                    }
                    if (!hourlyPay.getText().isEmpty()) {
                        updateAdmin(ids, "hourlyPay", e.getHourlyPay());
                    }
                    if (!occupation.getText().isEmpty()) {
                        updateAdmin(ids, "occupation", e.getOccupation());
                    }
                    if (!id.getText().isEmpty()) {
                        updateAdmin(ids, "adminID", e.getId());
                    }
                    return e;
                }
                return null;
            });

            Optional<Employee> result = dialog.showAndWait();

            adminTable.getItems().clear();
            adminTable.refresh();
            data = null;
            data = fillAdminCollection();
            adminTable.getItems().addAll(data);
            adminTable.refresh();

            //TODO what's next?
            if (result.isPresent()) {
                //deleteEmployee(result.get());
            }
        });

        menuBar.getMenus().add(viewMenu);
        menuBar.getMenus().add(employeesMenu);

        employees.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../EmployeesView.fxml"));
            BorderPane root;
            Scene employeeScene = null;
            try {
                root = loader.load();
                employeeScene = new Scene(root, 600, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
            EmployeesController employeesController = loader.getController();

            employeesController.setPrimaryStage(primaryStage, employeeScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMinHeight(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(employeeScene);
        });

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

            inventoryController.setPrimaryStage(primaryStage, inventoryScene, mainStageController, employeesCollection, currentSession);
            primaryStage.setMinHeight(600);
            primaryStage.setMaxHeight(600);
            primaryStage.setScene(inventoryScene);
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
        adminTable = new TableView<>();
        adminTable.setEditable(true);

        TableColumn<Employee, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(event -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue()));

        TableColumn<Employee, String> adminID = new TableColumn<>("Admin ID");
        adminID.setCellValueFactory(new PropertyValueFactory<>("id"));
        adminID.setCellFactory(TextFieldTableCell.forTableColumn());

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

        adminTable.getColumns().setAll(name, adminID, weeklyHours, password, occupation, hourlyPay);

        return adminTable;
    }

    private void addAdmin(Employee e) {
        try {
            collection.insertOne(new Document("name", e.getName()).append("weeklyHours", Integer.parseInt(e.getWeeklyHours())).append("employeeID", Integer.parseInt(e.getId()) + 30000).append("password", e.getPassword())
                    .append("hourlyPay", Integer.parseInt(e.getHourlyPay())).append("occupation", e.getOccupation()));
            adminTable.getItems().clear();
            data = null;
            data = fillAdminCollection();
            adminTable.getItems().addAll(data);
        } catch (NumberFormatException ex) {
            showAlertFillInfo();
        }
    }

    private void deleteAdmin(int e) {
        collection.deleteOne(eq("employeeID", e));
    }

    private void updateAdmin(int id, String updateField, String updateValue) {
        collection.updateOne(eq("employeeID", id), new Document("$set", new Document(updateField, updateValue)));
    }

    private void showAlertFillInfo() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Complete all fields", ButtonType.OK);
        alert.setTitle("Admin not completed");
        alert.show();
    }

    private Node createPage(Integer pageIndex) {
        int rowsPerPage = 10;
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, (int) collection.countDocuments());

        adminTable.getItems().setAll(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return adminTable;
    }

    private void showAlertInvalidInput() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid ID/Password, please try again.", ButtonType.OK);
        alert.setTitle("Invalid Input");
        alert.show();
    }

    private ObservableList<Employee> fillAdminCollection() {
        ObservableList<Employee> data = FXCollections.observableArrayList();
        List<DBObject> dbObjects;
        DBCursor cursor = dbCollection.find();
        dbObjects = cursor.toArray();

        for (DBObject obj : dbObjects) {
            Employee admin = MainStageController.getEmployee(obj);
            data.add(admin);
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
            System.out.println("Exception caught when trying to log out");
        }
    }
}
