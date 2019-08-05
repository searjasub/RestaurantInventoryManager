package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import models.OrderedItem;

public class FinancialTracker {
	//Basic idea of what this should be
	
	Pagination myPagination = new Pagination();
	
	TableView table = new TableView();
	
	ObservableList<OrderedItem> master = FXCollections.observableArrayList();
	
	int pageSize = 50;
	
	InventoryTrackerController tracker = new InventoryTrackerController();
	
	
	public void init(){
		//Method to get all inventory items and set them to the ObservableList needed
		master = tracker.reviewOrderedItems();


		myPagination.setPageFactory(this::createPage);
	}


	@SuppressWarnings("unchecked")
	public Node createPage(int pageIndex) {
		int first = pageIndex * pageSize;
		int last = Math.min(first + pageSize, master.size());
		table.setItems(FXCollections.observableArrayList(master.subList(first, last)));

		return table;
	}
	
	
	//Methods to add:
	
	//Method(s) that allow the user to switch between seeing only Assets, Liabilities, and Capital investments as well as a view that allows them to see all three
	//Should be in chronological order

	//Method that allows the user to click on the name of an item, and then view all items of that type along with their asset/liability values and the dates 
	//they were put in and their expiration date
	
	
	
	
	
}
