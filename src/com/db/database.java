package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * database
 */
public class database {
    static final String URL = String.format("jdbc:mysql://10.20.0.11:3306/" + System.getenv("MYSQL_DATABASE")); // "jdbc:mysql://172.30.207.35:1433/userdb";
    static final String USER = "root";
    static final String PWD = System.getenv("MYSQL_ROOT_PASSWORD");

    static Statement myStatement = null;

    public database() {

        try {
            // Connect to database
            Connection myConnection = DriverManager.getConnection(URL, USER, PWD);

            // Create a statememt
            this.myStatement = myConnection.createStatement();

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            System.err.println("Es konnte keine Verbindung zur Datenbank aufgebaut werden.");
            System.exit(1);
        }

    }

    public static Statement getMyStatement() {
        return myStatement;
    }

    public static void setMyStatement(Statement myStatement) {
        database.myStatement = myStatement;
    }
}