package controller;

import model.Employee;

public class CurrentSession {

    private Employee loggedIn;

    public CurrentSession(Employee loggedIn) {
        this.setLoggedIn(loggedIn);
    }

    public Employee getLoggedIn() {
        if (loggedIn == null){
            throw new IllegalArgumentException("There is no one currently logged in");
        }
        return loggedIn;
    }

    public void setLoggedIn(Employee loggedIn) {
        if (loggedIn == null){
            throw new IllegalArgumentException("There is no employee");
        }
        this.loggedIn = loggedIn;
    }

    public void restartSession(){
        loggedIn = null;
    }
}
