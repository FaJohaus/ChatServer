package com.muc;

public class ServerMain {
    public static void main(String[] args) {

        int port = 8080;

        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (Exception e) {
            System.out.println("The port is invalid!!!!!");
            System.exit(1);
        }

        Server server = new Server(port);
        server.start();
    }
}
