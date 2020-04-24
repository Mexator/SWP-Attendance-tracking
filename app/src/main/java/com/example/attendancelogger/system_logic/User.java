package com.example.attendancelogger.system_logic;

import androidx.annotation.NonNull;

import com.example.attendancelogger.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents user of attendance tracking system
 */
public class User {
    private static User instance = new User();
    private User(){}
    public static User getInstance() {
        return instance;
    }

    public enum Roles {PROFESSOR, STUDENT, ADMIN, UNKNOWN_ROLE};

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
        if(role.toLowerCase().equals("professor") || role.toLowerCase().equals("teacher"))
            return Roles.PROFESSOR;
        if(role.toLowerCase().equals("administrator"))
            return Roles.ADMIN;
        return Roles.UNKNOWN_ROLE;
    }

    public static void parseUser(JSONObject user) throws JSONException {
        String name = user.getString("first_name") + user.getString("last_name");
        Long ID = user.getLong("id");
        User.Roles role = User.parseRole(user.getString("role"));
        User.getInstance().init(name,ID,role);
    }

    private static int getLoginPath(Roles role) {
        int ret = -1;
        switch (role){
            case STUDENT:
                ret = R.id.studentMain;
                break;
            case ADMIN:
            case PROFESSOR:
                ret = R.id.professorMain;
        }
        return ret;
    }

    public int getLoginPath(){
        return getLoginPath(this.role);
    }
}