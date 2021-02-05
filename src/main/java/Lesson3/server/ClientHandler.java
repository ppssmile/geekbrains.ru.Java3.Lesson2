package Lesson3.server;

import Lesson3.DB.ConnectionService;
import Lesson3.client.LocalHistoryRecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler {
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private Chat chat;

    public ClientHandler(Socket socket, Chat chat) {
        this.socket = socket;
        try {
            socket.setSoTimeout(120000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.chat = chat;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }

        listen();
    }

    public String getName() {
        return name;
    }

    private void listen() {
        new Thread(() -> {
            doAuth();
            receiveMessage();
        }).start();
    }

    private void doAuth() {
        sendMessage("Please enter credentials. Sample [-auth login password]");
        try {
            while (true) {
                String mayBeCredentials = in.readUTF();
                if (mayBeCredentials.startsWith("-auth")) {
                    String[] credentials = mayBeCredentials.split("\\s");
                    boolean mayBeNickname = chat.getAuthenticationService(credentials[1], credentials[2]);
                    if (mayBeNickname) {
                        if (!chat.isNicknameOccupied(credentials[1])) {
                            sendMessage("[INFO] Auth OK");
                            socket.setSoTimeout(0);
                            name = credentials[1];
                            chat.broadcastMessage(String.format("[%s] logged in", credentials[1]));
                            chat.subscribe(this);
                            LocalHistoryRecord.isExistToFile(credentials[1]);
                            sendMessage(LocalHistoryRecord.doLoadingHistory(credentials[1]));
                            return;
                        } else {
                            sendMessage("[INFO] Current user is already logged in.");
                        }
                    } else {
                        sendMessage("[INFO] Wrong login or password.");
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public void receiveMessage() {
        System.out.println("enter to receiveMSG");
        while (true) {
            try {
                String message = in.readUTF();
                exitChat(message);
                if (message.startsWith("-rename")) {
                    userRename(message);
                    continue;
                }

                /**
                 *  reading file and loading to ClientChat
                 */

                LocalHistoryRecord.doWriteIntoFile(this.name, message);
                chat.broadcastMessage(String.format("[%s]: %s", name, message));
            } catch (ArrayIndexOutOfBoundsException a) {
                sendMessage("[INFO] Login cannot be empty!!!");
            } catch (IOException e) {
                throw new RuntimeException("SWW", e);
            }
        }
    }

    private boolean userRename(String str) {
        String newName = str.split(" ")[1];
        if (!chat.isNicknameOccupied(newName)) {
            if (ConnectionService.updateUsersName(this, name, newName)) {
                chat.broadcastMessage(String.format("[%s] rename to %s", name, newName));
                name = newName;
                return true;
            }
        }

        return false;
    }

    private void exitChat(String str) {
        if (str.startsWith("-exit")) {
            chat.unsubscribe(this);
            chat.broadcastMessage(String.format("[%s] logged out", name));
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}