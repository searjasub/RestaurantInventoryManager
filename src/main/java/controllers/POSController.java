package controllers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.Meal;
import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class POSController {

    MongoClient mc = new MongoClient();
    MongoDatabase database = mc.getDatabase("Restaurants");
    MongoCollection<Document> collection = database.getCollection("Meals");

    private static double salesTax;
    ArrayList<Meal> meals = new ArrayList<Meal>();
    private double totalCost;
    private double tip;
    private int orderNumber;

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


}
