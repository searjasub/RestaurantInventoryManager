package model;

import enums.FinanceType;

public class FinanceItem {

    private String id;
    private String name;
    private String amount;
    private String cost;
    private FinanceType type;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public FinanceType getType() {
        return type;
    }

    public void setType(FinanceType type) {
        this.type = type;
    }
}
