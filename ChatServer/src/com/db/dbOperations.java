package com.db;

import com.utils.cmdUtil;
import java.sql.*;
import java.util.ArrayList;

public class dbOperations {
    private static Statement myStatement;
    private static database a = new database();

    //Java Methoden zum Ausführen aller CRUD-Operationen zur Datenbank
    //TODO Starter Methoden für Create, Delete und Update schreiben, dass man sie nicht imemr mit executesqlquery vorher aufrufen muss
    private static void initStatement(){
        database a = new database();
        myStatement = a.getMyStatement();
    }

    public static void executeSqlQuery(String query){
        initStatement();
        //Execute Query String (send commands to the db)
        try {
            myStatement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Fehler beim Senden eines Befehls an die Datenbank");
        }
    }

    public static String writeData(String dbname, String[] columns, String[] values){
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

    public static String updateData(String dbname, String indicator, String indicatorValue, String column2change, String newValue){
        return "update "+dbname
                +   " set "+column2change+"='"+newValue+"'"
                +   " where "+indicator+"='"+indicatorValue+"'";
    }

    public static String deleteData(String dbname, String indicator, String indicatorValue){
        return "delete from "+dbname+" where "+indicator+"='"+indicatorValue+"'";
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
}
