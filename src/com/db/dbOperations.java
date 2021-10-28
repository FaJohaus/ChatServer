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

    private static String writeDataString(String table, String[] columns, String[] values){
        //Klebe alle Zeilen, in die geschrieben werden soll, mit ihren entsprechenden Werten zu einem query String zusammen
        String s = "insert into "+table
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
    public static void writeData(String table, String[] columns, String[] values){
        executeSqlQuery(writeDataString(table, columns, values));
    }


    private static String updateDataString(String table, String indicator, String indicatorValue, String column2change, String newValue){
        return "update "+table
                +   " set "+column2change+"='"+newValue+"'"
                +   " where "+indicator+"='"+indicatorValue+"'";
    }
    public static void updateData(String table, String indicator, String indicatorValue, String column2change, String newValue){
        executeSqlQuery(updateDataString(table, indicator, indicatorValue, column2change, newValue));
    }


    private static String deleteDataString(String table, String indicator, String indicatorValue){
        return "delete from "+table+" where "+indicator+"='"+indicatorValue+"'";
    }
    public static void deleteData(String table, String indicator, String indicatorValue){
        executeSqlQuery(deleteDataString(table, indicator, indicatorValue));
    }


    public static ArrayList<String> readColumn(String table, String column){
        //Lese eine gesamte Spalte aus der Datenbank aus
        initStatement();
        ArrayList<String> data = new ArrayList<String>();
        try {
            ResultSet myResultSet = myStatement.executeQuery("select * from "+table);
            while(myResultSet.next()){
                data.add(myResultSet.getString(column));
            }
        } catch (Exception e){
            System.err.println("Daten können nicht aus der Datenbank gelesen werden.");
        }

        return data;
    }

    public static String readValue(String table, String indicator, String indicatorValue, String column){
        //Lese einen Wert aus einer bestimmten Spalte, wo in der selben Zeile ein Wert an einer bestimmten Spalte einen bestimmten Wert hat
        //(z.B. lese aus der Spalte Passwort, wo in der gleichen Zeile der Wert der Spalte Name "Steffen" ist)
        initStatement();
        String s = null;
        try {
            ResultSet myResultSet = myStatement.executeQuery("select "+column+"" +
                    " from "+table+"" +
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

    private static String createTableString(String tableName, String columnName, int varcharSize){
        //Diese Methode kann nur einen table, mit einer Spalte erstellen, das ist aber für die Anwendung im ChatServer ausreichend
        return "CREATE TABLE IF NOT EXISTS `"+tableName+"` (" +
                "  `"+columnName+"` VARCHAR("+varcharSize+") NULL);";
    }
    public static void createTable(String tableName, String columnName, int varcharSize){
        executeSqlQuery(createTableString(tableName, columnName, varcharSize));
    }

    private static String deleteTableString(String tableName){
        return "DROP TABLE " + tableName;
    }
    public static void deleteTable(String tableName){
        executeSqlQuery(deleteTableString(tableName));
    }

    public static boolean tableExists(String tableName){
        initStatement();
        String s = null;
        try {
            ResultSet myResultSet = myStatement.executeQuery("SHOW TABLES LIKE " + "'"+tableName+"';");
            return myResultSet.next();

        } catch (SQLException throwables) {
            System.err.println("Daten können nicht aus der Datenbank gelesen werden.");
            return false;
        }
    }

    public static void changeTableName(String oldName, String newName){
        String s = "ALTER TABLE `"+oldName+"` " +
                "RENAME TO `"+newName+"` ;";
        executeSqlQuery(s);
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

    //Spezifische Methoden für groups
    public static boolean isMemberOfGroup(String group, String user){
        boolean userInGroup = false;
        for (String member: dbOperations.readColumn("group"+group, "members")) {
            if(member.equals(user)){
                userInGroup = true;
            }
        }
        return userInGroup;
    }

    public static void addUsersToGroup(String group, ArrayList<String> members){
        for (String member: members) {
            //Erstelle table für alle Gruppen des Nutzers, wenn er noch keinen hat
            if(!dbOperations.tableExists("groupsof"+member)){
                dbOperations.createTable("groupsof"+member, "chatGroups", 31);
            }
            //Füge diese Gruppe hinzu
            dbOperations.writeData("groupsof"+member, new String[]{"chatGroups"}, new String[]{group});

            //Füge den Nutzer im table der Gruppe hinzu
            dbOperations.writeData("group"+group, new String[]{"members"}, new String[]{member});
        }
    }
}
