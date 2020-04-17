package com.example.attendancelogger.system_logic;

import com.example.attendancelogger.NameID;

import org.json.JSONException;
import org.json.JSONObject;

public class Class implements NameID {
    private String name;
    private Long id;
    public Class(String name, long id){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getID() {
        return id;
    }
    public String toString(){
        return getName();
    }

    public static Class parseClass(JSONObject object) throws JSONException {
        return new Class(
                object.getString("title"),
                object.getLong("id"));
    }
}
