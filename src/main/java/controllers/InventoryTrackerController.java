package controllers;

import javafx.collections.ObservableList;
import models.Ingredient;
import models.OrderedItem;
import java.util.ArrayList;
import java.util.Date;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.Block;
import static com.mongodb.client.model.Filters.*;


public class InventoryTrackerController {
    MongoClient mc = new MongoClient("localHost");
    MongoDatabase database = mc.getDatabase("Restaurants");
    MongoCollection<Document> collection = database.getCollection("Inventory");

    public void addItem(String ingredientName, int itemId, Date prepDate, Date expDate, int caloriePerServing,
    int amount, int individualCost, int bulkCost, int bulkAmount) {
        collection.insertOne(new Document("ingredientName", ingredientName).append("ingredientID", itemId).append("prepDate", prepDate)
        .append("expDate", expDate).append("caloriePerServing", caloriePerServing).append("amount", amount).append("individualCost", individualCost)
        .append("bulkCost", bulkCost).append("bulkAmount", bulkAmount));
    }

    public void deleteItem(String itemName) {
        collection.deleteOne(eq("name", itemName));
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
