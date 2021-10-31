package com.db;

import java.sql.*;

/**
 * database
 */
public class database {
    static final String URL = String.format("jdbc:mysql://localhost:3306/" + System.getenv("MYSQL_DATABASE")); // "jdbc:mysql://172.30.207.35:1433/userdb";
    static final String USER = "root";
    static final String PWD = System.getenv("MYSQL_ROOT_PASSWORD");

    static Statement myStatement = null;

    public database() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Connect to database
            Connection myConnection = DriverManager.getConnection(URL, USER, PWD);

            // Create a statememt
            this.myStatement = myConnection.createStatement();

        } catch (SQLException | ClassNotFoundException throwables) {
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