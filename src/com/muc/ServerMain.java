package com.muc;

public class ServerMain {
    public static void main(String[] args) {

        int port = 8080;

        System.out.println(System.getenv("CHAT_PORT"));
        System.out.println(System.getenv("MYSQL_DATABASE"));
        System.out.println(System.getenv("MYSQL_ROOT_PASSWORD"));
        try {
            port = Integer.parseInt(System.getenv("CHAT_PORT"));
        } catch (Exception e) {
            System.out.println("The port is invalid!!!!!");
            System.exit(1);
        }

        Server server = new Server(port);
        server.start();
    }
}
