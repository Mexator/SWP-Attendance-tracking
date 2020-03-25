package com.example.attendancelogger;

import java.net.MalformedURLException;
import java.net.URL;

import javax.security.auth.login.LoginException;

/**
 * Represents API to interact with backend. Uses Singleton pattern
 */
public class AttendanceBackend {
    private static AttendanceBackend instance = new AttendanceBackend();
    private User user;
    private URL serverURL;

    private AttendanceBackend() {
        user = User.getInstance();
        try {
            serverURL = new URL("https://attendance-inno.herokuapp.com/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static AttendanceBackend getInstance() {
        return instance;
    }

    public void setServerURL(URL serverURL) {
        this.serverURL = serverURL;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public User getUser() {
        return user;
    }

    public void logIn(String username, String password) throws LoginException {
        //TODO: Change to real implementation
        User.Roles role;

        if (username.equals("professor"))
            role = User.Roles.PROFESSOR;
        else if (username.equals("student"))
            role = User.Roles.STUDENT;
        else throw new LoginException("Wrong login!");

        user.init(username, 1L, role);
    }

    private void sendRequest(String query){

    }
}
