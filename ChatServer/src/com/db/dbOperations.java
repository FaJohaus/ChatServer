package com.db;

import com.utils.cmdUtil;
import java.sql.*;
import java.util.ArrayList;

public class dbOperations {
    private static Statement myStatement;
    private static database a = new database();

    //Java Methoden zum Ausführen aller CRUD-Operationen zur Datenbank
    private static void initStatement(){
        database a = new database();
        myStatement = a.getMyStatement();
    }

    private static void executeSqlQuery(String query){
        initStatement();
        //Execute Query String (send commands to the db)
        try {
            myStatement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Fehler beim Senden eines Befehls an die Datenbank");
        }
    }

    private static String writeDataString(String dbname, String[] columns, String[] values){
        //Klebe alle Zeilen, in die geschrieben werden soll, mit ihren entsprechenden Werten zu einen query String zusammen
        String s = "insert into "+dbname
                +   " (";

        for (String column: columns) {
            s += column +", ";
        }
        s = s.substring(0, s.length() -2);
        s += ") values (";
        for (String value: values) {
            s += "'"+value+"'" + ", ";
        }
        s = s.substring(0, s.length() -2);
        s += ")";

        return s;
    }
    public static void writeData(String dbname, String[] columns, String[] values){
        executeSqlQuery(writeDataString(dbname, columns, values));
    }


    private static String updateDataString(String dbname, String indicator, String indicatorValue, String column2change, String newValue){
        return "update "+dbname
                +   " set "+column2change+"='"+newValue+"'"
                +   " where "+indicator+"='"+indicatorValue+"'";
    }
    public static void updateData(String dbname, String indicator, String indicatorValue, String column2change, String newValue){
        executeSqlQuery(updateDataString(dbname, indicator, indicatorValue, column2change, newValue));
    }


    private static String deleteDataString(String dbname, String indicator, String indicatorValue){
        return "delete from "+dbname+" where "+indicator+"='"+indicatorValue+"'";
    }
    public static void deleteData(String dbname, String indicator, String indicatorValue){
        executeSqlQuery(deleteDataString(dbname, indicator, indicatorValue));
    }


    public static ArrayList<String> readColumn(String dbname, String column){
        //Lese eine gesamte Spalte aus der Datenbank aus
        initStatement();
        ArrayList<String> data = new ArrayList<String>();
        try {
            ResultSet myResultSet = myStatement.executeQuery("select * from "+dbname);
            while(myResultSet.next()){
                data.add(myResultSet.getString(column));
            }
        } catch (Exception e){
            System.err.println("Daten können nicht aus der Datenbank gelesen werden.");
        }

        return data;
    }

    public static String readValue(String dbname, String indicator, String indicatorValue, String column){
        //Lese einen Wert aus einer bestimmten Spalte, wo in der selben Zeile ein Wert an einer bestimmten Spalte einen bestimmten Wert hat
        //(z.B. lese aus der Spalte Passwort, wo in der gleichen Zeile der Wert der Spalte Name "Steffen" ist)
        initStatement();
        String s = null;
        try {
            ResultSet myResultSet = myStatement.executeQuery("select "+column+"" +
                    " from "+dbname+"" +
                    " where "+indicator+"='"+indicatorValue+"'");

            ArrayList<String> data = new ArrayList<String>();
            try {
                while(myResultSet.next()){
                    data.add(myResultSet.getString(column));
                }

                s = data.get(0);
            } catch (Exception e){
                System.err.println("Daten können nicht aus der Datenbank gelesen werden.");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return s;
    }

    //Spezifische Methoden für users
    public static boolean userExists(String name2check){
        ArrayList<String> userNames = readColumn("users", "name");

        boolean userExists = false;
        for (String name: userNames) {
            if (name.equals(name2check)) {
                userExists = true;
            }
        }
        return userExists;
    }

    public static boolean pwdCorrect(String user2check, String pwd2check){
        String pwd = readValue("users", "name", user2check, "pwd");
        if(pwd.equals(pwd2check)){return true;}
        else{return false;}
    }
}
