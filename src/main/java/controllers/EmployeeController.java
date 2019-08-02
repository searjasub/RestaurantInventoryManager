package controllers;

import models.Employee;

import java.util.Date;
import java.util.HashMap;
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

public class EmployeeController {

    MongoClient mc = new MongoClient();

    HashMap<Integer, Employee> empsCollection = new HashMap<Integer, Employee>();

    public void AddEmployee(int id, String name, double hourlyPay, String occupation) {

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
}
