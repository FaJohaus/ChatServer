package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.utils.colors.*;
import com.utils.chatutils.rgbChat;

public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private rgbChat rgbChat = null;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        rgbChat = new rgbChat(false);
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
        while ((line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("quit".equalsIgnoreCase(cmd) || "logoff".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else if ("send".equalsIgnoreCase(cmd)) {
                    server.sendToAll(line.substring(4));
                } else if ("msg".equalsIgnoreCase(cmd)) {

                    msg(outputStream, tokens);
                } else if ("rbf".equals(cmd)) {
                    rgbChat.setActive(!rgbChat.isActive());
                    send("Now with rainbow effect");
                }

                else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        clientSocket.close();
    }

    private void handleLogoff() {
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

    private void handleLogin(OutputStream outputStream, String[] tokens) {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if (login.equals("guest") && password.equals("guest") || login.equals("jim") && password.equals("jim")) {
                String msg = Colorss.ANSI_BRIGHT_RED.getsit() + "ok login\n";
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
        if (login != null) {
            try {
                outputStream.write(rgbChat.Contiues_RGB(msg + "\r\n").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

}
