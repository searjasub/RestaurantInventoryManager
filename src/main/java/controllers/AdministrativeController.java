package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import models.Employee;

import java.util.Date;
import java.util.HashMap;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import models.OrderedItem;
import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

public class AdministrativeController {

    MongoClient mc = new MongoClient();

    HashMap<Integer, Employee> empsCollection = new HashMap<Integer, Employee>();

    public void AddEmployee(int id, String name, double hourlyPay, String occupation,String password) {

    }

    public void deleteEmployee(int id) {

    }

    public void updateEmployee(int id) {

    }

    public void startBreak(Date time, int id) {

    }

    public void endBreak(Date time, int id) {

    }

    public void clockIn(int id, Date time) {

    }

    public void clockOut(int id, Date time) {

    }

    public void login(int id, String password) {

    }

    public void fillEmpCollection() {

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
