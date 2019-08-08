package model;

import java.util.ArrayList;

public class Meal {

    ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private String name;
    private double cost;
    private boolean veganFriendly;
    private double totalCalorieCount;
    private int mealId;

    public Meal(String name, int mealId) {
        setName(name);
        setMealId(mealId);
        setVeganFriendly(determineIfVF());
        setCost(calculateTotalCost());
    }

    public boolean determineIfVF() {
        boolean returnVal = true;
        for (Ingredient i : ingredients) {
            if (!i.isVeganFriendly()) {
                returnVal = false;
                break;
            }
        }
        return returnVal;
    }

    public double calculateTotalCost() {
        double finalCost = 0.0d;

        for (Ingredient i : ingredients) {
            double temp = i.getCostPerIngredient();
            finalCost += temp;
        }

        return finalCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isVeganFriendly() {
        return veganFriendly;
    }

    public void setVeganFriendly(boolean veganFriendly) {
        this.veganFriendly = veganFriendly;
    }

    public double getTotalCalorieCount() {
        return totalCalorieCount;
    }

    public void setTotalCalorieCount(double totalCalorieCount) {
        this.totalCalorieCount = totalCalorieCount;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

}
