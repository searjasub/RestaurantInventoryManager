package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Meal {

    ArrayList<Ingredient> ingredients = new ArrayList<>();
    private StringProperty name;
    private StringProperty cost;
    private StringProperty veganFriendly;
    private StringProperty totalCalorie;
    private StringProperty mealID;

    public Meal(){

    }

    public Meal(String name, String mealID) {
        setName(name);
        setMealID(mealID);
        setVeganFriendly(determineIfVF());
        setCost(calculateTotalCost());
    }


    public String determineIfVF() {
        String returnVal = "true";
        for (Ingredient i : ingredients) {
            if (!i.isVeganFriendly()) {
                returnVal = "false";
                break;
            }
        }
        return returnVal;
    }

    public String calculateTotalCost() {
        double finalCost = 0.0d;
        String stringVal;
        for (Ingredient i : ingredients) {
            String temp = i.getCostPerIngredient();
            Double temp1 = Double.valueOf(temp);
            finalCost += temp1;
        }
        stringVal = Double.toHexString(finalCost);
        return stringVal;
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    private StringProperty nameProperty(){
        if (name == null) {
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }

    public String getCost() {
        return costProperty().get();
    }

    public void setCost(String cost) {
        costProperty().set(cost);
    }

    private StringProperty costProperty(){
        if (cost == null) {
            cost = new SimpleStringProperty(this, "cost");
        }
        return cost;
    }

    public String isVeganFriendly() {
        return veganFriendlyProperty().get();
    }

    public void setVeganFriendly(String veganFriendly) { veganFriendlyProperty().set(veganFriendly); }

    private StringProperty veganFriendlyProperty(){
        if (veganFriendly == null) {
            veganFriendly = new SimpleStringProperty(this, "veganFriendly");
        }
        return veganFriendly;
    }

    public String getTotalCalorieCount() { return totalCalorieCountProperty().get(); }

    public void setTotalCalorieCount(String totalCalorieCount) {
        totalCalorieCountProperty().set(totalCalorieCount);
    }

    private StringProperty totalCalorieCountProperty(){
        if (totalCalorie == null) {
            totalCalorie = new SimpleStringProperty(this, "totalCalorie");
        }
        return totalCalorie;
    }

    public String getMealId() {return mealIDProperty().get(); }

    public void setMealID(String mealID) {
        mealIDProperty().set(mealID);
    }

    private StringProperty mealIDProperty(){
        if (mealID == null) {
            mealID = new SimpleStringProperty(this, "mealID");
        }
        return mealID;
    }

    public String getBulkCost(){
        return "20.0";
    }


}
