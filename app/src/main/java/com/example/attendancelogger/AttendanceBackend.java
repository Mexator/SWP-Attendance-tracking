package com.example.attendancelogger;

import javax.security.auth.login.LoginException;

/**
 * Represents API to interact with backend. Uses Singleton pattern
 */
public class AttendanceBackend{
    private static AttendanceBackend instance = new AttendanceBackend();
    private User user;
    private AttendanceBackend(){
        user = User.getInstance();
    }
    public static AttendanceBackend getInstance() {
        return instance;
    }

    public boolean isLoggedIn(){
        return user != null;
    }
    public User getUser() {
        return user;
    }

    public void logIn(String username, String password) throws LoginException{
        //TODO: Change to real implementation
        User.Roles role;

        if(username.equals("professor"))
            role = User.Roles.PROFESSOR;
        else if(username.equals("student"))
            role = User.Roles.STUDENT;
        else throw new LoginException("Wrong login!");

        user.init(username,1L,role);
    }
    public void markAsAttended(){
    }
}
