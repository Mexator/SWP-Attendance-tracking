package com.example.attendancelogger;

import androidx.annotation.NonNull;

/**
 * Represents user of attendance tracking system
 */
public class User {
    private static User instance = new User();
    private User(){}
    public static User getInstance() {
        return instance;
    }

    public enum Roles {PROFESSOR, STUDENT, ADMIN};

    private Long ID;
    private Roles role;
    private String name;

    public String getName() {
        return instance.name;
    }
    public Roles getRole(){
        return instance.role;
    }
    public Long getID() {
        return instance.ID;
    }

    public void init(@NonNull String name,@NonNull Long ID,@NonNull Roles role){
        instance.name = name;
        instance.ID = ID;
        instance.role = role;
    }

    public static Roles parseRole(String role){
        if(role.toLowerCase().equals("student"))
            return Roles.STUDENT;
        if(role.toLowerCase().equals("professor"))
            return Roles.PROFESSOR;
        if(role.toLowerCase().equals("administrator"))
            return Roles.ADMIN;
        return null;
    }
}