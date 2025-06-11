package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BSTVisualizer extends JFrame {

    private TreeNode root;
    private final JPanel treePanel;
    private final Map<TreeNode, Color> nodeColors = new HashMap<>();

    private final Color DEFAULT_COLOR = new Color(60, 120, 200);
    private final Color SEARCH_PATH_COLOR = Color.YELLOW;
    private final Color FOUND_COLOR = new Color(0, 200, 100);

    public BSTVisualizer() {
        setTitle("Binary Search Tree Visualizer");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Binary Search Tree (BST)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        treePanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTree(g, root, getWidth() / 2, 40, getWidth() / 4);
            }
        };
        treePanel.setBackground(new Color(20, 20, 20));
        add(treePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        controlPanel.setLayout(new FlowLayout());

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton insertBtn = createStyledButton("Insert", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                root = insert(root, value);
                nodeColors.clear();
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
                new Thread(() -> {
                    searchWithAnimation(root, value);
                }).start();
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

    private TreeNode insert(TreeNode node, int value) {
        if (node == null) {
            TreeNode newNode = new TreeNode(value);
            nodeColors.put(newNode, DEFAULT_COLOR);
            return newNode;
        }
        if (value < node.value) node.left = insert(node.left, value);
        else if (value > node.value) node.right = insert(node.right, value);
        return node;
    }

    private void searchWithAnimation(TreeNode node, int value) {
        resetColors(root);
        while (node != null) {
            nodeColors.put(node, SEARCH_PATH_COLOR);
            repaint();
            sleep(500);

            if (node.value == value) {
                nodeColors.put(node, FOUND_COLOR);
                repaint();
                return;
            }
            nodeColors.put(node, SEARCH_PATH_COLOR);
            node = value < node.value ? node.left : node.right;
        }
    }

    private void resetColors(TreeNode node) {
        if (node == null) return;
        nodeColors.put(node, DEFAULT_COLOR);
        resetColors(node.left);
        resetColors(node.right);
        repaint();
    }

    private void drawTree(Graphics g, TreeNode node, int x, int y, int offset) {
        if (node == null) return;

        Color color = nodeColors.getOrDefault(node, DEFAULT_COLOR);

        g.setColor(color);
        g.fillOval(x - 20, y - 20, 40, 40);
        g.setColor(Color.BLACK);
        g.drawOval(x - 20, y - 20, 40, 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString(String.valueOf(node.value), x - 8, y + 5);

        if (node.left != null) {
            g.setColor(Color.WHITE);
            g.drawLine(x, y + 20, x - offset, y + 70 - 20);
            drawTree(g, node.left, x - offset, y + 70, offset / 2);
        }

        if (node.right != null) {
            g.setColor(Color.WHITE);
            g.drawLine(x, y + 20, x + offset, y + 70 - 20);
            drawTree(g, node.right, x + offset, y + 70, offset / 2);
        }
    }

    private static class TreeNode {
        int value;
        TreeNode left, right;

        TreeNode(int val) {
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

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
