package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class StackVisualizer extends JFrame {

    private final Stack<Integer> stack = new Stack<>();
    private final JPanel stackPanel;
    private final JLabel topLabel;

    public StackVisualizer() {
        setTitle("Stack Visualizer");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Stack (LIFO) - Last In First Out", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBackground(new Color(30, 30, 30));
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        stackPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawStack(g);
            }
        };
        stackPanel.setBackground(new Color(20, 20, 20));
        add(stackPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        controlPanel.setLayout(new FlowLayout());

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton pushBtn = createStyledButton("Push", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                stack.push(value);
                inputField.setText("");
                updateTopLabel();
                repaint();
            } catch (NumberFormatException e) {
                showError("Enter a valid integer");
            }
        });

        JButton popBtn = createStyledButton("Pop", () -> {
            if (!stack.isEmpty()) {
                stack.pop();
                updateTopLabel();
                repaint();
            } else {
                showError("Stack is empty!");
            }
        });

        JButton peekBtn = createStyledButton("Peek", () -> {
            if (!stack.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Top: " + stack.peek());
            } else {
                showError("Stack is empty!");
            }
        });

        JButton backBtn = createStyledButton("Back to Menu", () -> {
            this.dispose();
            new ui.DataStructuresVisualizer();
        });


        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(inputField);
        controlPanel.add(pushBtn);
        controlPanel.add(popBtn);
        controlPanel.add(peekBtn);
        controlPanel.add(backBtn);
        add(controlPanel, BorderLayout.SOUTH);

        topLabel = new JLabel("Top: [empty]", SwingConstants.CENTER);
        topLabel.setForeground(Color.WHITE);
        topLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        topLabel.setBackground(new Color(40, 40, 40));
        topLabel.setOpaque(true);
        topLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topLabel, BorderLayout.EAST);

        setVisible(true);
    }

    private void drawStack(Graphics g) {
        int width = stackPanel.getWidth() - 40;
        int height = 40;
        int gap = 10;
        int baseY = stackPanel.getHeight() - 50;

        for (int i = 0; i < stack.size(); i++) {
            int y = baseY - i * (height + gap);
            g.setColor(new Color(60, 120, 200));
            g.fillRect(20, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(20, y, width, height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            String value = String.valueOf(stack.get(i));
            g.drawString(value, 30, y + 25);
        }
    }

    private void updateTopLabel() {
        SwingUtilities.invokeLater(() -> {
            if (stack.isEmpty()) {
                topLabel.setText("Top: [empty]");
            } else {
                topLabel.setText("Top: " + stack.peek());
            }
        });
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

}
