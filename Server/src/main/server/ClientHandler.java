package main.server;

import main.shared.Message;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // This is a list of all the clients connected to the server
    private Socket socket; // This is the socket for communication with the client
    private ObjectInputStream oin; // This is the input stream for receiving messages from the client
    private ObjectOutputStream oout; // This is the output stream for sending messages to the client
    private String username; // This is the username of the client
    private Color color; // This is the color of the client (randomly assigned)

    public ClientHandler(Socket socket) {
        try {
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());

            this.socket = socket;
            this.username = (String) oin.readObject(); // Read the username of the client
            this.color = getNewColor(); // Generate a random color for the client
            clientHandlers.add(this); // Add the client to the list of connected clients

            System.out.println("A new client connected: " + username);
            broadcastMessage(new Message("Server", username + " has joined the chat", Message.MessageType.CONNECT, color)); // Broadcast a message to all clients that a new client has joined
        } catch (Exception e) {
            closeEverything(socket, oin, oout); // Close resources if an exception occurs
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = (String) oin.readObject(); // Read the message from the client
                System.out.println(username + ": " + messageFromClient);
                broadcastMessage(new Message(username, messageFromClient, Message.MessageType.MESSAGE, color)); // Broadcast the message to all connected clients
            } catch (IOException | ClassNotFoundException e) {
                closeEverything(socket, oin, oout); // Close resources if an exception occurs
                break;
            }
        }
    }

    public void broadcastMessage(Message message) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler == this && message.getType() != Message.MessageType.CONNECT) {
                continue; // Skip the current client
            }

            try {
                if (clientHandler.socket.isConnected()) {
                    clientHandler.oout.writeObject(message);
                    clientHandler.oout.flush();
                }
            } catch (IOException e) {
                closeEverything(clientHandler.socket, clientHandler.oin, clientHandler.oout);
            }
        }
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

        disconnectClient();
    }

    public void disconnectClient() {
        clientHandlers.remove(this);
        broadcastMessage(new Message("Server", username + " has left the chat", Message.MessageType.DISCONNECT, color));
    }

    public Color getNewColor() {
        // Generate a random color for the client
        // Make sure that the color doesn't match the background color and has enough contrast with it
        // Background colors: #DCF8C6 (light green), #C2F0FC (light blue)
        Color bgColor1 = new Color(0xDC, 0xF8, 0xC6);
        Color bgColor2 = new Color(0xC2, 0xF0, 0xFC);
        Color[] bgColors = {bgColor1, bgColor2};

        Color newColor;
        do {
            float hue = (float) Math.random();
            float saturation = 0.5f + (float) Math.random() * 0.5f; // 0.5 to 1.0
            float brightness = 0.6f + (float) Math.random() * 0.4f; // 0.6 to 1.0
            newColor = Color.getHSBColor(hue, saturation, brightness);
        } while (!hasEnoughContrast(newColor, bgColors));

        return newColor;
    }

    private boolean hasEnoughContrast(Color color, Color[] bgColors) {
        for (Color bgColor : bgColors) {
            if (color.equals(bgColor)) {
                return false;
            }

            double luminance1 = getLuminance(color);
            double luminance2 = getLuminance(bgColor);
            double contrast = (Math.max(luminance1, luminance2) + 0.05) / (Math.min(luminance1, luminance2) + 0.05);
            if (contrast < 3.0) {
                return false;
            }
        }

        return true;
    }

    private double getLuminance(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        r = (r <= 0.03928) ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
        g = (g <= 0.03928) ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
        b = (b <= 0.03928) ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }
}
