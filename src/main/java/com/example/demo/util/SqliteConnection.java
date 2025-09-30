package com.example.demo.util;

import java.sql.*;

public class SqliteConnection {
    private static Connection instance = null;

    private SqliteConnection() {
        String url = "jdbc:sqlite:ReadRacer.db";
        try {
            instance = DriverManager.getConnection(url);
            System.out.println("Connection established");
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteConnection();
        }
        return instance;
    }

}
