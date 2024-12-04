package main.client;

import javax.swing.*;
import java.awt.*;

public class ChatBubble extends JPanel {
    protected String message;
    protected String sender;
    protected Color color;
    protected JLabel messageLabel;
    protected JLabel messageSenderLabel;
    protected boolean senderVisible = true;

    public ChatBubble(String message, String sender, Color color) {
        this.message = message;
        this.sender = sender;
        this.color = color;

        setBackground(sender != null ? Color.decode("#DCF8C6") : Color.decode("#C2F0FC"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Padding for the chat bubble
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        addSender();

        // Format the message
        this.message = "<html><div style='width: 200px;'>" + message + "</div></html>";
        messageLabel = new JLabel(this.message);
        add(messageLabel);
    }

    public void joinMessage(String message) {
        // Insert the new message into the existing message (before the closing </div> tag)
        this.message = this.message.substring(0, this.message.length() - 13) + "<br>" + message + "</div></html>";

        removeAll();
        if (senderVisible) {
            addSender();
        }
        JLabel messageLabel = new JLabel(this.message);
        add(messageLabel);
    }

    public void addSender() {
        messageSenderLabel = new JLabel(sender != null ? sender : "You");
        messageSenderLabel.setFont(new Font(messageSenderLabel.getFont().getName(), Font.BOLD, messageSenderLabel.getFont().getSize()));
        messageSenderLabel.setForeground(color != null ? color : Color.GRAY);
        add(messageSenderLabel);
    }

    public String getMessage() {
        return message;
    }
    public String getSender() {
        return sender;
    }
}
