package controller;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Meal;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoController {

    private MongoClient mc = new MongoClient();
    private MongoClient mongoC = new MongoClient(new ServerAddress("Localhost", 27017));
    private DB db = mongoC.getDB("Restaurants");
    private MongoDatabase database = mc.getDatabase("Restaurants");
    private DBCollection dbCollection = db.getCollection("Employees");
    private MongoCollection<Document> collection = database.getCollection("Employees");
    private MongoCollection<Document> adminCollection = database.getCollection("Administrators");
    private DBCollection adminDbCollection = db.getCollection("Administrators");
    private MongoCollection<Document> mealCollection = database.getCollection("Meals");
    private DBCollection mealDbCollection = db.getCollection("Meals");

    public HashMap<Integer, Employee> fillEmpCollection() {
        HashMap<Integer, Employee> data = new HashMap<>();
        List<DBObject> dbObjects = new ArrayList<>();
        int id = 100001;
        for (int i = 0; i < 5; i++) {
            DBObject query = BasicDBObjectBuilder.start().add("employeeID", id + i).get();
            DBCursor cursor = dbCollection.find(query);
            while (cursor.hasNext()) {
                dbObjects.add(cursor.next());
            }
        }

        Employee employee;
        for (int i = 0; i < collection.countDocuments(); i++) {
            employee = new Employee();
            employee.setName(dbObjects.get(i).get("name").toString());
            employee.setPassword(dbObjects.get(i).get("password").toString());
            employee.setOccupation(dbObjects.get(i).get("occupation").toString());
            employee.setWeeklyHours(dbObjects.get(i).get("weeklyHours").toString());
            employee.setId(dbObjects.get(i).get("employeeID").toString());
            employee.setHourlyPay(dbObjects.get(i).get("hourlyPay").toString());
            data.put(Integer.parseInt(employee.getId()), employee);
        }
        return data;
    }

    public HashMap<Integer, Employee> fillAdminCollection() {
        HashMap<Integer, Employee> data = new HashMap<>();

        List<DBObject> dbObjects = new ArrayList<>();

        int id = 30000;
        for (int i = 0; i < adminCollection.countDocuments(); i++) {
            DBObject query1 = BasicDBObjectBuilder.start().add("employeeID", "" + (id + i)).get();
            DBCursor cursor = adminDbCollection.find(query1);
            while (cursor.hasNext()) {
                dbObjects.add(cursor.next());
            }
        }

        Employee employee;
        for (int i = 0; i < adminCollection.countDocuments(); i++) {
            employee = new Employee();
            employee.setName(dbObjects.get(i).get("name").toString());
            employee.setPassword(dbObjects.get(i).get("password").toString());
            employee.setOccupation(dbObjects.get(i).get("occupation").toString());
            employee.setWeeklyHours(dbObjects.get(i).get("weeklyHours").toString());
            employee.setId(dbObjects.get(i).get("employeeID").toString());
            employee.setHourlyPay(dbObjects.get(i).get("hourlyPay").toString());
            data.put(Integer.parseInt(employee.getId()), employee);
        }
        return data;
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
            meal.setTotalCalorieCount(dbObjects.get(i).get("totalCalorie").toString());
            meal.setVeganFriendly(dbObjects.get(i).get("veganFriendly").toString());
            data.add(meal);
        }
        return data;
    }

    public void updateMeal(int mealId, String updateField, String updateValue) {
        collection.updateOne(eq("mealID", mealId), new Document("$set", new Document(updateField, updateValue)));
    }

    public void deleteMeal(int mealId) {
        collection.deleteOne(eq("mealID", mealId));
    }


    private void AddEmployee(Employee e) {
        collection.insertOne(new Document("name", e.getName()).append("employeeID", Integer.parseInt(e.getId())).append("password", e.getPassword())
                .append("hourlyPay", Integer.parseInt(e.getHourlyPay())).append("occupation", e.getOccupation()));
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
        obj = new ArrayList<BasicDBObject>();

        obj.add(new BasicDBObject("employeeID", id));
        obj.add(new BasicDBObject("password", password));
        andQuery.put("$and", obj);
        collection.find(andQuery);
    }
}
