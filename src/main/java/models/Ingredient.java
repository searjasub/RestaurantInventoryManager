package models;

import java.util.Date;

public class Ingredient {

	private boolean veganFriendly;
	private Date expiredDate;
	private Date prepDate;
	private double caloriePerServing;
	private double costPerIngredient;
	private double bulkCost;
	private String name;
	private int ingredientId;
	private int amount;
	
	private Ingredient(int ingredientID,String name,boolean veganFriendly, Date prepDate, Date expiredDate,double caloriePerServing,double costPerIngredient, int amount) {
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
	public double getCaloriePerServing() {
		return caloriePerServing;
	}
	public void setCaloriePerServing(double caloriePerServing) {
		this.caloriePerServing = caloriePerServing;
	}
	public double getCostPerIngredient() {
		return costPerIngredient;
	}
	public void setCostPerIngredient(double costPerIngredient) {
		this.costPerIngredient = costPerIngredient;
	}
	public double getBulkCost() {
		return bulkCost;
	}
	public void setBulkCost(double bulkCost) {
		this.bulkCost = bulkCost;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIngredientId() {
		return ingredientId;
	}
	public void setIngredientId(int ingredientId) {
		this.ingredientId = ingredientId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	
}
