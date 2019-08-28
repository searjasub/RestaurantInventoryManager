package model;

import enums.FinanceType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FinanceItem {

    private StringProperty id;
    private StringProperty name;
    private StringProperty amount;
    private StringProperty cost;
    private StringProperty stringType;
    private FinanceType type;

    public String getCost() {
        return costProperty().get();
    }

    public void setCost(String cost) {
        costProperty().set(cost);
    }


    private StringProperty costProperty() {
        if (cost == null) {
            cost = new SimpleStringProperty(this, "cost");
        }
        return cost;
    }

    public String getStringType() { return typeProperty().get(); }

    public void setStringType(String type) { typeProperty().set(type.toString()); }

    private StringProperty typeProperty(){
        if(stringType == null){
            String v = this.type.toString();
            this.stringType = new SimpleStringProperty(v);
        }
        return stringType;
    }


    public String getId() {
        return idProperty().get();
    }

    public void setId(String id) {
        idProperty().set(id);
    }

    private StringProperty idProperty() {
        if (id == null) {
            id = new SimpleStringProperty(this, "id");
        }
        return id;
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    private StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }

    public String getAmount() {
        return amountProperty().get();
    }

    public void setAmount(String amount) {
        amountProperty().set(amount);
    }

    private StringProperty amountProperty() {
        if (amount == null) {
            amount = new SimpleStringProperty(this, "amount");
        }
        return amount;
    }

    public FinanceType getType() {
        return type;
    }

    public void setType(FinanceType type) {
        this.type = type;
    }
}
