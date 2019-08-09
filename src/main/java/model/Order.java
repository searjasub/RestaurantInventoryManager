package model;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class Order {

   public HashMap<Integer, String> meals = new HashMap();
    double orderCost = 0.0;
    double calorieCount = 0.0;
    int orderId;
    MongoClient mc = new MongoClient();
    MongoDatabase database = mc.getDatabase("Restaurants");
    MongoCollection<Document> collection = database.getCollection("Meals");


    public void addMeal(int mealID){
//        meals.put()
        collection.find(eq("mealId", mealID));
    }
    public void removeMeal(int mealID){

    }
    public void getMealData(int mealID){

    }
    public void updateMeal(int mealID, String field, String updatedInfo){

    }

    public double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(double orderCost) {
        this.orderCost = orderCost;
    }

    public double getCalorieCount() {
        return calorieCount;
    }

    public void setCalorieCount(double calorieCount) {
        this.calorieCount = calorieCount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
