package main.shared;

import java.awt.*;
import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    private String content;
    private MessageType type;
    private Color color;

    public enum MessageType {
        CONNECT, DISCONNECT, MESSAGE
    }

    public Message(String sender, String content, MessageType type, Color color) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.color = color;
    }

    public String getSender() {
        return sender;
    }
    public String getContent() {
        return content;
    }
    public MessageType getType() {
        return type;
    }
    public Color getColor() {
        return color;
    }
}
