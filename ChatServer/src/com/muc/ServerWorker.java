package com.muc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.handlers.generalCommands;
import com.handlers.rgbCommands;
import com.handlers.dbCommands;
import com.utils.chatutils.commandhandler.commandHandler;
import com.utils.chatutils.rgbChatUtil.rgbChat;
import com.utils.colors.Colorsbg;
import com.utils.colors.Colorss;

import com.db.dbOperations;

public class ServerWorker extends Thread {
    public final Socket clientSocket;
    public final Server server;
    public String login = null;
    public OutputStream outputStream;
    public rgbChat rgbChat = null;
    public commandHandler[] cH = null;
    public boolean active = true;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        rgbChat = new rgbChat(false);

        cH = new commandHandler[] { new rgbCommands(this), new generalCommands(this), new dbCommands(this) };
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null || active) {
            boolean success = false;

            if (line == null || line.trim().length() == 0)
                continue;

            String[] parts = line.trim().split(" ");
            String cmd = parts[0];
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            for (commandHandler cHandler : cH) {

                if (cHandler.handler(cmd, args)) {
                    success = true;
                    break;
                }
            }
            if (!success) {
                send("This was no valid command");
            }

        }
        clientSocket.close();
    }

    public void handleLogoff() {
        List<ServerWorker> workerList = server.getWorkerList();
        // send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return login;
    }

    public void handleLogin(OutputStream outputStream, String[] tokens) {
        if (tokens.length == 2) {
            String login = tokens[0];
            String password = tokens[1];

            if(dbOperations.userExists(login) && dbOperations.pwdCorrect(login, password)) {
                String msg = Colorss.ANSI_BRIGHT_RED.getsit() + "Erfolgreich angemeldet als "+login+"\n";
                try {
                    outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.login = login;
                System.out.println("User logged in succsesfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();

                // send current user all other online logins
                for (ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }
                // send other online users current user's status
                String onlineMsg = "online " + login + "\n";
                for (ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            } else {
                String msg = "error login\n";
                try {
                    outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void send(String msg) {
        try {
            outputStream.write(rgbChat.Contiues_RGB(msg + "\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void msg(OutputStream out, String[] msg) {
        if (login != null) {

            for (ServerWorker worker : server.workerList) {

                if (worker.getLogin().equals(msg[1])) {
                    msg[0] = "";
                    msg[1] = "";
                    worker.send("User " + this.getLogin() + " send u a private message:\n" + String.join(" ", msg));
                    return;
                }

            }
            this.send(Colorsbg.ANSI_BG_GREEN.getsit() + Colorss.ANSI_RED.getsit() + "Nix funktioniernen ");
        }

    }

    public void sendTo(String receiver, String msg){
        if (login != null){
            for (ServerWorker worker: server.workerList) {
                if(worker.getLogin().equals(receiver)){
                    worker.send(msg);
                }
            }
        }
    }
}
