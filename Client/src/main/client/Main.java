package main.client;

import main.shared.Message;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

import static main.shared.ServerConfig.HOST;
import static main.shared.ServerConfig.PORT;

public class Main extends JFrame {
    private static Socket socket;
    private static Client client;

    private static ChatPanel chatPanel;
    private JPanel messagePanel;
    private JTextField messageField;
    private JButton sendButton;

    public Main() {
        // Add the ChatPanel to the JFrame
        chatPanel = new ChatPanel();
        add(chatPanel);

        // Add the MessagePanel to the JFrame
        messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        add(messagePanel, BorderLayout.SOUTH);

        // Add the MessageField to the MessagePanel
        messageField = new JTextField();
        messagePanel.add(messageField, BorderLayout.CENTER);

        // Add the SendButton to the MessagePanel
        sendButton = new JButton("Send");
        messagePanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(e -> sendMessage());

        // Ask for the username. If the user closes the dialog or chooses "Cancel", the program will exit.
        // But if they keep the field empty and click "OK", they will be prompted to enter a username again.
        String username;
        do {
            username = JOptionPane.showInputDialog(this, "Enter your username", "Username", JOptionPane.PLAIN_MESSAGE);
            if (username == null) {
                System.exit(0);
            }
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (username == null || username.isEmpty());
        client = new Client(socket, username, chatPanel);

        // Set the JFrame properties
        setTitle("Chat Client - " + username);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket = new Socket(HOST, PORT);

        SwingUtilities.invokeLater(Main::new);
    }

    public void sendMessage() {
        if (messageField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = messageField.getText();
        chatPanel.addMessage(new Message(null, message, Message.MessageType.MESSAGE, null));
        messageField.setText("");
        client.sendMessage(message);
    }
}
