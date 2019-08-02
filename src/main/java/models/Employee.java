package models;

import java.util.Date;

public class Employee {

    private Date clockIn;
    private Date clockOut;
    private Date breakStart;
    private Date breakEnd;
    private double hourlyPay;
    private String name;
    private String password;
    private String occupation;
    private int id;
    private int weekyHours;

    public Employee(int id, String name, String password, String occupation, double hourlyPay) {
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeekyHours() {
        return weekyHours;
    }

    public void setWeekyHours(int weekyHours) {
        this.weekyHours = weekyHours;
    }

    public double getHourlyPay() {
        return hourlyPay;
    }

    public void setHourlyPay(double hourlyPay) {
        this.hourlyPay = hourlyPay;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }


}
