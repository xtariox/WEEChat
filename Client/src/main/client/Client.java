package main.client;

import main.shared.Message;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;
    private String username;
    private ChatPanel chatPanel;

    public Client(Socket socket, String username, ChatPanel chatPanel) {
        try {
            this.socket = socket;
            this.oout = new ObjectOutputStream(socket.getOutputStream());
            this.oin = new ObjectInputStream(socket.getInputStream());
            this.username = username;
            this.chatPanel = chatPanel;

            oout.writeObject(username);
            oout.flush();

            listenForMessages();
        }
        catch (IOException e) {
            closeEverything(socket, oin, oout);
        }
    }

    public void sendMessage(String message) {
        try {
            oout.writeObject(message);
            oout.flush();
        }
        catch (IOException e) {
            closeEverything(socket, oin, oout);
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            Message messageFromServer;
            while (socket.isConnected()) {
                try {
                    messageFromServer = (Message) oin.readObject();
                    chatPanel.addMessage(messageFromServer);
                } catch (IOException e) {
                    closeEverything(socket, oin, oout);
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream oin, ObjectOutputStream oout) {
        try {
            if (oin != null) {
                oin.close();
            }
            if (oout != null) {
                oout.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
