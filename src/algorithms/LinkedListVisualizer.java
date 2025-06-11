package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LinkedListVisualizer extends JFrame {

    private Node head;
    private final JPanel listPanel;
    private final List<Node> nodeList = new ArrayList<>();

    private final Color DEFAULT_COLOR = new Color(60, 120, 200);
    private final Color FOUND_COLOR = new Color(0, 200, 100);
    private final Color TRAVERSING_COLOR = Color.YELLOW;

    public LinkedListVisualizer() {
        setTitle("Linked List Visualizer");
        setSize(1000, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Singly Linked List", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawList(g);
            }
        };
        listPanel.setBackground(new Color(20, 20, 20));
        add(listPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        controlPanel.setLayout(new FlowLayout());

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton insertBtn = createStyledButton("Insert", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                insert(value);
                inputField.setText("");
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
        Node newNode = new Node(value);
        newNode.color = DEFAULT_COLOR;
        nodeList.add(newNode);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
    }

    private void search(int value) {
        Node current = head;
        while (current != null) {
            current.color = TRAVERSING_COLOR;
            repaint();
            sleep(500);
            if (current.value == value) {
                current.color = FOUND_COLOR;
                repaint();
                return;
            }
            current.color = DEFAULT_COLOR;
            current = current.next;
        }
    }

    private void drawList(Graphics g) {
        int x = 30;
        for (Node node : nodeList) {
            g.setColor(node.color);
            g.fillRect(x, 100, 60, 40);
            g.setColor(Color.BLACK);
            g.drawRect(x, 100, 60, 40);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(node.value), x + 22, 125);
            if (node.next != null) {
                g.setColor(Color.WHITE);
                g.drawLine(x + 60, 120, x + 80, 120);
                g.drawLine(x + 75, 115, x + 80, 120);
                g.drawLine(x + 75, 125, x + 80, 120);
            }
            x += 90;
        }
    }

    private static class Node {
        int value;
        Node next;
        Color color;

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
