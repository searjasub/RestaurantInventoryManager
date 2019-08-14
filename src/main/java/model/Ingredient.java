package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class Ingredient {

    private boolean veganFriendly;
    private Date expiredDate;
    private Date prepDate;
    private StringProperty caloriePerServing;
    private StringProperty costPerIngredient;
    private StringProperty bulkCost;
    private StringProperty name;
    private StringProperty ingredientId;
    private StringProperty amount;

    public Ingredient(){};

    public Ingredient(String ingredientID, String name, boolean veganFriendly, Date prepDate, Date expiredDate, String caloriePerServing, String costPerIngredient, String amount) {
        setVeganFriendly(veganFriendly);
        setPrepDate(prepDate);
        setExpiredDate(expiredDate);
        setCaloriePerServing(caloriePerServing);
        setCostPerIngredient(costPerIngredient);
        setName(name);
        setIngredientId(ingredientID);
        setAmount(amount);
    }

    public boolean isVeganFriendly() {
        return veganFriendly;
    }

    public void setVeganFriendly(boolean veganFriendly) {
        this.veganFriendly = veganFriendly;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Date getPrepDate() {
        return prepDate;
    }

    public void setPrepDate(Date prepDate) {
        this.prepDate = prepDate;
    }

    private StringProperty calorieProperty(){
        if (caloriePerServing == null) {
            caloriePerServing = new SimpleStringProperty(this, "caloriePerServing");
        }
        return caloriePerServing;
    }

    public String getCaloriePerServing() {
        return calorieProperty().get();
    }

    public void setCaloriePerServing(String caloriePerServing){
        calorieProperty().set(caloriePerServing);
    }
    private StringProperty costProperty(){
        if (costPerIngredient == null) {
            costPerIngredient = new SimpleStringProperty(this, "costPerIngredient");
        }
        return costPerIngredient;
    }

    public String getCostPerIngredient() {
        return costProperty().get();
    }

    public void setCostPerIngredient(String costPerIngredient) {
        costProperty().set(costPerIngredient);
    }
    private StringProperty bulkCostProperty(){
        if (bulkCost == null) {
            bulkCost = new SimpleStringProperty(this, "bulkCost");
        }
        return bulkCost;
    }

    public String getBulkCost() {
        return bulkCostProperty().get();
    }

    public void setBulkCost(String bulkCost) {
        bulkCostProperty().set(bulkCost);
    }

    public String getName() {
        return nameProperty().get();
    }
    private StringProperty nameProperty(){
        if (name == null) {
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }

    public void setName(String name) {
        nameProperty().set(name);
    }
    private StringProperty ingredientIdProperty(){
        if (ingredientId == null) {
            ingredientId = new SimpleStringProperty(this, "ingredientId");
        }
        return ingredientId;
    }

    public String getIngredientId() {
        return ingredientIdProperty().get();
    }

    public void setIngredientId(String ingredientId) {
        ingredientIdProperty().set(ingredientId);
    }

    private StringProperty amountProperty(){
        if (amount == null) {
            amount = new SimpleStringProperty(this, "amount");
        }
        return amount;
    }

    public String getAmount() {
        return amountProperty().get();
    }

    public void setAmount(String amount) {
        amountProperty().set(amount);
    }


}