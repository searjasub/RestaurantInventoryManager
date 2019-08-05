package controllers;

import javafx.collections.ObservableList;
import models.Ingredient;
import models.OrderedItem;

import java.util.ArrayList;
import java.util.Date;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

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

public class InventoryTrackerController {
    MongoClient mc = new MongoClient("localHost");
    MongoDatabase database = mc.getDatabase("Restaurants");
    MongoCollection<Document> collection = database.getCollection("Inventory");

    public void addItem(String ingredientName, int itemId, Date prepDate, Date expDate, int caloriePerServing,
    int amount, int individualCost, int bulkCost, int bulkAmount) {

    }

    public void deleteItem(int itemId, int amount) {
        System.out.println(collection.deleteOne(eq("ingredientID", itemId)));
    }

    public void updateItem(int itemId, String updateField, String updateValue) {
        collection.updateOne(eq("ingredientID", itemId), new Document("$set", new Document(updateField, updateValue)));
    }

    public void getItemData(int itemId) {
        collection.find(eq("ingredientID", itemId)).forEach(printBlock);
    }

    public ArrayList<Ingredient> soonToExpire() {
        return null;
    }

    public void retrieveDailySales() {

    }

    public void retrieveWeeklySales(Date startDay) {

    }

    public ObservableList<OrderedItem> reviewOrderedItems(){
    return null;
    }


    Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document);
        }
    };

}
