package com.jwolfe.ankyl.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInteractionDialog  extends JDialog implements ActionListener {
    JButton button;
    String syncMessage;

    public UserInteractionDialog() {

    }

    public void show(final String message) {
        syncMessage = message;
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setTitle("User Intervention Required");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 5, 5));

        JTextArea messageArea = new JTextArea(message);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        panel.add(messageArea);

        JButton btn = new JButton("Complete Interaction");
        btn.addActionListener(this);
        btn.setPreferredSize(new Dimension(140, 27));
        btn.setMaximumSize(new Dimension(140, 27));
        panel.add(btn);

        add(panel);

        setSize(400, 400);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        synchronized (syncMessage) {
            syncMessage.notifyAll();
        }

        dispose();
    }
}
