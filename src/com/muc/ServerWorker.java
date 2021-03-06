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

import com.db.dbOperations;
import com.handlers.dbCommands;
import com.handlers.generalCommands;
import com.handlers.rgbCommands;
import com.utils.chatutils.commandhandler.commandHandler;
import com.utils.chatutils.rgbChatUtil.rgbChat;
import com.utils.colors.Colorss;

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

    public void handleQuit() {
        List<ServerWorker> workerList = server.getWorkerList();
        // send other online users current user's status
        String onlineMsg = login + " ist nun offline.";
        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }

        // Setze den lastonl in der db auf die akutelle Zeit
        dbOperations.updateData("users", "name", login, "lastonl", String.valueOf(System.currentTimeMillis()));
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLogout() {
        List<ServerWorker> workerList = server.getWorkerList();
        // send other online users current user's status
        String onlineMsg = login + " ist nun offline.";
        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        // Setze den lastonl in der db auf die akutelle Zeit
        dbOperations.updateData("users", "name", getLogin(), "lastonl", String.valueOf(System.currentTimeMillis()));

        send("Du bist nun von " + getLogin()
                + " abgemeldet. Melde dich wieder mit einem Account an, um den ChatServer benutzen zu k??nnen.");
        login = null;
    }

    public String getLogin() {
        return login;
    }

    public void handleLogin(OutputStream outputStream, String[] tokens) {
        if (tokens.length == 2) {
            String login = tokens[0];
            String password = tokens[1];

            if (dbOperations.userExists(login) && dbOperations.pwdCorrect(login, password)) {
                String msg = Colorss.ANSI_BRIGHT_RED.getsit() + "Erfolgreich angemeldet als " + login + "\n";
                try {
                    outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.login = login;
                System.out.println("User logged in succsesfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();

                // send other online users current user's status
                String onlineMsg = login + " ist nun online.";
                for (ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }

                // Setze den lastonl in der db auf "0" (soll hei??en, der Nutzer ist online)
                dbOperations.updateData("users", "name", login, "lastonl", "online");
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

    public boolean loginIsNull() {
        if (login == null)
            return true;
        return false;
    }

    public boolean equalsLogin(String s) {
        return login.equals(s);
    }

    public void send(String msg) {
        if (!loginIsNull()) {
            try {
                outputStream.write(rgbChat.Contiues_RGB(msg + "\r\n").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send2Null(String msg) {
        try {
            outputStream.write(rgbChat.Contiues_RGB(msg + "\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTo(String receiver, String msg) {
        for (ServerWorker worker2getMsg : server.workerList) {
            if (worker2getMsg.getLogin().equals(receiver)) {
                worker2getMsg.send(msg);
            }
        }
    }

    public void sendToWithDBsafe(String receiver, String message) {

        // ??berpr??fe, ob der Empf??nger existiert
        if (!dbOperations.userExists(receiver)) {
            send("Der Nutzer " + receiver + " existiert nicht.");
            return;
        }

        // ??berpr??fe, ob der Empf??nger online ist, wenn nicht speichere die Nachricht in
        // der db
        if (dbOperations.readValue("users", "name", receiver, "lastonl").equals("online")) {
            sendTo(receiver, message);
        } else {
            if (message.length() > 1000) {
                send(receiver
                        + " ist grade nicht online und deine Nachricht ist zu lang zum speichern, bitte fasse dich etwas k??rzer.");
            }
            dbOperations.createTable("messages" + receiver, "messages", 1000);
            dbOperations.writeData("messages" + receiver, new String[] { "messages" }, new String[] { message });
            send(receiver
                    + " ist grade nicht online, deine Nachricht wurde gespeichert und er/sie kann sie lesen, wenn er/sie online ist.");
        }
    }

    public void sendToAll(String msg) {
        for (ServerWorker worker : server.workerList) {
            worker.send(msg);
        }
    }
}
