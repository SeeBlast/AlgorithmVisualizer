package ui;

import algorithms.SortingVisualizer;
import algorithms.SearchingVisualizer;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Algorithm Visualizer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(20, 20, 20));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Algorithm Visualizer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel("By Juan Sebastian Pinto Ossio");
        authorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        authorLabel.setForeground(Color.LIGHT_GRAY);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(Box.createVerticalStrut(15));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(authorLabel);
        titlePanel.add(Box.createVerticalStrut(15));

        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        menuPanel.setBackground(new Color(30, 30, 30));

        JButton sortingBtn = createStyledButton("Sorting Algorithms", SortingVisualizer::new);
        JButton searchingBtn = createStyledButton("Searching Algorithms", SearchingVisualizer::new);
        JButton dataStructuresBtn = createStyledButton("Data Structures", DataStructuresVisualizer::new);
        JButton advancedBtn = createStyledButton("Advanced Algorithms", AdvancedAlgorithmsVisualizer::new);

        menuPanel.add(sortingBtn);
        menuPanel.add(searchingBtn);
        menuPanel.add(dataStructuresBtn);
        menuPanel.add(advancedBtn);

        add(titlePanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);

        getContentPane().setBackground(new Color(20, 20, 20));
        setVisible(true);
    }

    private JButton createStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(new Color(60, 120, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            MainMenu.this.dispose();
            action.run();
        });
        return button;
    }


}
