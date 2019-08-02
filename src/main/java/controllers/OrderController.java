package controllers;

import models.Meal;

import java.util.ArrayList;

public class OrderController {

    private static double salesTax;
    ArrayList<Meal> meals = new ArrayList<Meal>();
    private double totalCost;
    private double tip;
    private int orderNumber;

    public static double getSalesTax() {
        return salesTax;
    }

    public static void setSalesTax(double salesTax) {
        OrderController.salesTax = salesTax;
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
