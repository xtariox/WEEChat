package main.client;

import java.awt.*;

public class JoinBubble extends ChatBubble {
    public JoinBubble(String message) {
        super(message, "Join", null);

        setBackground(Color.decode("#9DE3C4"));
        remove(messageSenderLabel); // Remove the sender label
        senderVisible = false;

        this.message = message;
        messageLabel.setText(message);

        revalidate();
        repaint();
    }
}
