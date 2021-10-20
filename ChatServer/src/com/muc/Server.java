package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.db.database;

public class Server extends Thread {
    private final int serverPort;

    public ArrayList<ServerWorker> workerList = new ArrayList<>();

    public Server(int serverPort) {
        //Überprüfe Datenbankverbindung
        database a = new database();
        Statement myStatement = a.getMyStatement();
        if(myStatement == null){
            System.err.println("Es konnte keine Verbindung zur Datenbank aufgebaut werden.");
            System.exit(1);
        }

        this.serverPort = serverPort;
    }

    public List<ServerWorker> getWorkerList() {
        return workerList;
    }

    public void sendToAll(String msg) {
        for (ServerWorker s : workerList) {
            s.send(msg + "\n");
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("About to accept client connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted Connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
