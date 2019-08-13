package model;

import javafx.beans.property.*;

import java.util.Date;

public class Employee {

    private Date clockIn;
    private Date clockOut;
    private Date breakStart;
    private Date breakEnd;
    private StringProperty hourlyPay;
    private StringProperty name;
    private StringProperty password;
    private StringProperty occupation;
    private StringProperty id;
    private StringProperty weeklyHours;

    public Employee() {
    }

    public Employee(String id, String name, String password, String occupation, String hourlyPay) {
        setId(id);
        setName(name);
        setPassword(password);
        setOccupation(occupation);
        setHourlyPay(hourlyPay);
    }

    public Date getClockIn() {
        return clockIn;
    }

    public void setClockIn(Date clockIn) {
        this.clockIn = clockIn;
    }

    public Date getClockOut() {
        return clockOut;
    }

    public void setClockOut(Date clockOut) {
        this.clockOut = clockOut;
    }

    public Date getBreakStart() {
        return breakStart;
    }

    public void setBreakStart(Date breakStart) {
        this.breakStart = breakStart;
    }

    public Date getBreakEnd() {
        return breakEnd;
    }

    public void setBreakEnd(Date breakEnd) {
        this.breakEnd = breakEnd;
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

    public String getPassword() {
        return passwordProperty().get();
    }

    public void setPassword(String password) {
        passwordProperty().set(password);
    }

    private StringProperty passwordProperty(){
        if (password == null) {
            password = new SimpleStringProperty(this, "password");
        }
        return password;
    }

    public String getId() {
        return idProperty().get();
    }

    public void setId(String id) {
        idProperty().set(id);
    }

    private StringProperty idProperty(){
        if (id == null) {
            id = new SimpleStringProperty(this, "id");
        }
        return id;
    }

    public String getWeeklyHours() {
        return weeklyHoursProperty().get();
    }

    public void setWeeklyHours(String weeklyHours) {
        weeklyHoursProperty().set(weeklyHours);
    }

    private StringProperty weeklyHoursProperty(){
        if (weeklyHours == null) {
            weeklyHours = new SimpleStringProperty(this, "weeklyHours");
        }
        return weeklyHours;
    }

    public String getHourlyPay() {
        return hourlyPayProperty().get();
    }

    private void setHourlyPay(String hourlyPay) {
        hourlyPayProperty().set(hourlyPay);
    }

    private StringProperty hourlyPayProperty(){
        if (hourlyPay == null) {
            hourlyPay = new SimpleStringProperty(this, "hourlyPay");
        }
        return hourlyPay;
    }

    public String getOccupation() {
        return occupationProperty().get();
    }

    public void setOccupation(String occupation) {
        occupationProperty().set(occupation);
    }

    private StringProperty occupationProperty(){
        if (occupation == null) {
            occupation = new SimpleStringProperty(this, "occupation");
        }
        return occupation;
    }

    @Override
    public String toString() {
        return "Employee: " + getName() + " ID: " + getId();
    }
}
