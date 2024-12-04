package main.client;

import java.awt.*;

public class LeaveBubble extends ChatBubble {
    public LeaveBubble(String message) {
        super(message, "Leave", null);

        setBackground(Color.decode("#FFC0CB"));
        remove(messageSenderLabel); // Remove the sender label
        senderVisible = false;

        this.message = message;
        messageLabel.setText(message);

        revalidate();
        repaint();
    }
}
