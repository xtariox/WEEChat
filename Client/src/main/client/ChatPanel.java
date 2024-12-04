package main.client;

import main.shared.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ChatPanel extends JPanel {
    private JScrollPane chatScrollPane;
    private JPanel chatPanel;
    private ChatBubble lastChatBubble;
    private int scrollSpeed = 10;

    public ChatPanel() {
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        chatScrollPane = new JScrollPane(chatPanel);
        add(chatScrollPane, BorderLayout.CENTER);

        chatPanel.setBackground(Color.LIGHT_GRAY);

        chatScrollPane.addMouseWheelListener(new MouseWheelListener() {
            private Timer timer;
            private int target;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }

                target = chatScrollPane.getVerticalScrollBar().getValue() + e.getUnitsToScroll() * 24;
                timer = new Timer(1, null);
                timer.addActionListener(e1 -> {
                    int current = chatScrollPane.getVerticalScrollBar().getValue();
                    int increment = (int) Math.ceil((double) (target - current) / scrollSpeed);
                    if (increment == 0) {
                        chatScrollPane.getVerticalScrollBar().setValue(target);
                        timer.stop();
                    } else {
                        chatScrollPane.getVerticalScrollBar().setValue(current + increment);
                    }
                });
                timer.start();
            }
        });
    }

    public void addMessage(Message message) {
        String lastSender = lastChatBubble != null ? lastChatBubble.getSender() : null;

        if (lastChatBubble != null && lastSender != null && lastSender.equals(message.getSender()) ||
                lastChatBubble != null && lastSender == null && message.getSender() == null) {

            lastChatBubble.joinMessage(message.getContent());
            chatPanel.revalidate();
            chatPanel.repaint();
            return;
        }

        if (lastChatBubble != null) {
            chatPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        ChatBubble chatBubble = switch (message.getType()) {
            case CONNECT -> new JoinBubble(message.getContent());
            case DISCONNECT -> new LeaveBubble(message.getContent());
            default -> new ChatBubble(message.getContent(), message.getSender(), message.getColor());
        };

        lastChatBubble = chatBubble;
        chatPanel.add(chatBubble);

        chatPanel.revalidate();
        chatPanel.repaint();

        smoothScrollToBottom();
    }

    private void smoothScrollToBottom() {
        final int target = chatScrollPane.getVerticalScrollBar().getMaximum();
        final Timer timer = new Timer(1, null);
        timer.addActionListener(e -> {
            int current = chatScrollPane.getVerticalScrollBar().getValue();
            int increment = (target - current) / 10;
            if (increment == 0) {
                chatScrollPane.getVerticalScrollBar().setValue(target);
                timer.stop();
            } else {
                chatScrollPane.getVerticalScrollBar().setValue(current + increment);
            }
        });
    }
}
