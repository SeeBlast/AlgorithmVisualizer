package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class HashTableVisualizer extends JFrame {

    private final int SIZE = 10;
    private final List<LinkedList<Node>> table = new ArrayList<>();
    private final Map<Node, Color> nodeColors = new HashMap<>();

    private final Color DEFAULT_COLOR = new Color(60, 120, 200);
    private final Color TRAVERSING_COLOR = Color.YELLOW;
    private final Color FOUND_COLOR = new Color(0, 200, 100);

    private final JPanel tablePanel;

    public HashTableVisualizer() {
        setTitle("Hash Table Visualizer");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        for (int i = 0; i < SIZE; i++) {
            table.add(new LinkedList<>());
        }

        JLabel title = new JLabel("Hash Table (Chaining)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        tablePanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTable(g);
            }
        };
        tablePanel.setBackground(new Color(20, 20, 20));
        add(tablePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        controlPanel.setLayout(new FlowLayout());

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton insertBtn = createStyledButton("Insert", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                inputField.setText("");
                insert(value);
                repaint();
            } catch (NumberFormatException e) {
                showError("Enter a valid integer");
            }
        });

        JButton searchBtn = createStyledButton("Search", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                inputField.setText("");
                new Thread(() -> search(value)).start();
            } catch (NumberFormatException e) {
                showError("Enter a valid integer");
            }
        });

        JButton backBtn = createStyledButton("Back to Menu", () -> {
            this.dispose();
            new ui.DataStructuresVisualizer();
        });

        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(inputField);
        controlPanel.add(insertBtn);
        controlPanel.add(searchBtn);
        controlPanel.add(backBtn);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void insert(int value) {
        int index = hash(value);
        Node newNode = new Node(value);
        nodeColors.put(newNode, DEFAULT_COLOR);
        table.get(index).add(newNode);
    }

    private void search(int value) {
        int index = hash(value);
        LinkedList<Node> chain = table.get(index);
        for (Node node : chain) {
            nodeColors.put(node, TRAVERSING_COLOR);
            repaint();
            sleep(500);
            if (node.value == value) {
                nodeColors.put(node, FOUND_COLOR);
                repaint();
                return;
            }
            nodeColors.put(node, DEFAULT_COLOR);
        }
    }

    private int hash(int value) {
        return value % SIZE;
    }

    private void drawTable(Graphics g) {
        int boxWidth = 80, boxHeight = 40, gap = 10, startX = 30;
        for (int i = 0; i < SIZE; i++) {
            int x = startX;
            int y = 50 + i * (boxHeight + gap);
            g.setColor(Color.GRAY);
            g.drawRect(x, y, boxWidth, boxHeight);
            g.setColor(Color.WHITE);
            g.drawString("Index " + i, x, y - 5);
            LinkedList<Node> chain = table.get(i);
            for (Node node : chain) {
                x += boxWidth + 5;
                g.setColor(nodeColors.getOrDefault(node, DEFAULT_COLOR));
                g.fillRect(x, y, boxWidth, boxHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, boxWidth, boxHeight);
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf(node.value), x + 25, y + 25);
            }
        }
    }

    private static class Node {
        int value;

        Node(int val) {
            this.value = val;
        }
    }

    private JButton createStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(60, 120, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
