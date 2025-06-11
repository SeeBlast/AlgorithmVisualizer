package ui;


import algorithms.*;

import javax.swing.*;
import java.awt.*;

public class DataStructuresVisualizer extends JFrame {

    public DataStructuresVisualizer() {
        setTitle("Data Structures Visualizer");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Data Structures Visualizer", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        optionsPanel.setBackground(new Color(20, 20, 20));

        optionsPanel.add(createStyledButton("Stack", StackVisualizer::new));
        optionsPanel.add(createStyledButton("Queue", QueueVisualizer::new));
        optionsPanel.add(createStyledButton("Linked List", LinkedListVisualizer::new));
        optionsPanel.add(createStyledButton("Binary Search Tree", BSTVisualizer::new));
        optionsPanel.add(createStyledButton("Hash Table", HashTableVisualizer::new));
        optionsPanel.add(createStyledButton("Set", SetVisualizer::new));

        add(optionsPanel, BorderLayout.CENTER);

        JButton backBtn = createStyledButton("Back to Menu", () -> {
            this.dispose();
            new ui.MainMenu();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(20, 20, 20));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        getContentPane().setBackground(new Color(20, 20, 20));
        setVisible(true);
    }

    private JButton createStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(60, 120, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            DataStructuresVisualizer.this.dispose();
            action.run();
        });
        return button;
    }

}
