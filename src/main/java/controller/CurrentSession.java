package controller;

import model.Employee;

public class CurrentSession {

    private Employee loggedIn;
    private boolean isAdmin;

    CurrentSession() {
    }

    public Employee getLoggedIn() {
        if (loggedIn == null) {
            throw new IllegalArgumentException("There is no one currently logged in");
        }
        return loggedIn;
    }

    void setLoggedIn(Employee loggedIn) {
        if (loggedIn == null) {
            throw new IllegalArgumentException("There is no employee");
        }
        this.loggedIn = loggedIn;
    }

    void restartSession() {
        loggedIn = null;
    }

    boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
