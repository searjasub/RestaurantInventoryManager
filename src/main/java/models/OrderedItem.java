package models;

import java.util.Date;

public class OrderedItem {

	private int ingredientId;
	private Date orderedDate;
	private Date expectedDeliveryDate;
	private boolean delivered;
	
	public OrderedItem(int IngredientId, Date OrderedDate, Date expectedDeliveryDate) {
		setIngredientId(IngredientId);
		setOrderedDate(OrderedDate);
		setExpectedDeliveryDate(expectedDeliveryDate);
	}
	
	public int getIngredientId() {
		return ingredientId;
	}
	public void setIngredientId(int ingredientId) {
		this.ingredientId = ingredientId;
	}
	public Date getOrderedDate() {
		return orderedDate;
	}
	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
	}
	public Date getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}
	public void setExpectedDeliveryDate(Date expectedDeliveryTime) {
		this.expectedDeliveryDate = expectedDeliveryTime;
	}
	public boolean isDelivered() {
		return delivered;
	}
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}
}
