package com.example.attendancelogger.system_logic;

import com.example.attendancelogger.NameID;

import org.json.JSONException;
import org.json.JSONObject;

public class Student implements NameID {
    private Long ID;
    private String name;

    public Student(String name,Long ID){
        this.name = name;
        this.ID = ID;
    }

    @Override
    public Long getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    public static Student parseStudent(JSONObject rawData) throws JSONException {
        String name = rawData.getString("first_name") + " "+ rawData.getString("last_name");
        Long ID = rawData.getLong("id");
        return new Student(name, ID);
    }
}
