package model;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class Order {

   public HashMap<Integer,Double> costs = new HashMap<>();
    private StringProperty orderCost;
    private StringProperty orderID;

    MongoClient mc = new MongoClient();
    MongoDatabase database = mc.getDatabase("Restaurants");
    MongoCollection<Document> collection = database.getCollection("Orders");
    MongoCollection<Document> mealCollection = database.getCollection("Meals");


    public void addMealCost(int mealID, double cost){
//        meals.put()
        costs.put(mealID,cost);

    }
    public void removeMeal(int mealID){
    costs.remove(mealID);
    }


    public String getOrderID() {return mealIDProperty().get(); }
    public void setOrderID(String orderID) {
        mealIDProperty().set(orderID);
    }
    private StringProperty mealIDProperty(){
        if (orderID == null) {
            orderID = new SimpleStringProperty(this, "orderID");
        }
        return orderID;
    }

    public String orderCost() {return orderCostProperty().get(); }
    public void setOrderCost(String orderCost) {
        orderCostProperty().set(orderCost);
    }
    private StringProperty orderCostProperty(){
        if (orderCost == null) {
            orderCost = new SimpleStringProperty(this, "orderCost");
        }
        return orderCost;
    }



}
