package com.example.attendancelogger;

/**
 * Interface represents everything that has name and ID:
 * Classes, Activities, etc.
 */
public interface NameID{
    Long getID();
    String getName();
    String toString();
}
