package com.swp.attendancelogger.system_logic;

/**
 * Interface represents everything that has name and ID:
 * Classes, Activities, etc.
 */
public interface NameID{
    Long getID();
    String getName();
    String toString();
}
