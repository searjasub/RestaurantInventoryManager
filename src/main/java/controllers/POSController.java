package controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Meal;

import java.util.ArrayList;

public class POSController {

    private static double salesTax;
    private ArrayList<Meal> meals = new ArrayList<Meal>();
    private double totalCost;
    private double tip;
    private int orderNumber;
    private Stage primaryStage;
    private Scene scene;
    private MainStageController mainController;

    void setPrimaryStage(Stage primaryStage, Scene tmp, MainStageController mainStageController) {
        this.primaryStage = primaryStage;
        this.scene = tmp;
        this.mainController = mainStageController;


    }

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

    public void updateMeal(int mealId) {

    }

    public void deleteMeal(int mealId) {

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
