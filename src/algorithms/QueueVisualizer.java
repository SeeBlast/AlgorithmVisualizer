package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class QueueVisualizer extends JFrame {

    private final Queue<Integer> queue = new LinkedList<>();
    private final JPanel queuePanel;
    private final JLabel frontLabel;

    public QueueVisualizer() {
        setTitle("Queue Visualizer");
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Queue (FIFO) - First In First Out", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBackground(new Color(30, 30, 30));
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        queuePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawQueue(g);
            }
        };
        queuePanel.setBackground(new Color(20, 20, 20));
        add(queuePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(30, 30, 30));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton enqueueBtn = createStyledButton("Enqueue", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                queue.add(value);
                inputField.setText("");
                updateFrontLabel();
                repaint();
            } catch (NumberFormatException e) {
                showError("Enter a valid integer");
            }
        });

        JButton dequeueBtn = createStyledButton("Dequeue", () -> {
            if (!queue.isEmpty()) {
                queue.poll();
                updateFrontLabel();
                repaint();
            } else {
                showError("Queue is empty!");
            }
        });

        JButton peekBtn = createStyledButton("Peek", () -> {
            if (!queue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Front: " + queue.peek());
            } else {
                showError("Queue is empty!");
            }
        });

        JButton backBtn = createStyledButton("Back to Menu", () -> {
            this.dispose();
            new ui.DataStructuresVisualizer();
        });

        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(inputField);
        controlPanel.add(enqueueBtn);
        controlPanel.add(dequeueBtn);
        controlPanel.add(peekBtn);
        controlPanel.add(backBtn);
        add(controlPanel, BorderLayout.SOUTH);

        frontLabel = new JLabel("Front: [empty]", SwingConstants.CENTER);
        frontLabel.setForeground(Color.WHITE);
        frontLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        frontLabel.setBackground(new Color(40, 40, 40));
        frontLabel.setOpaque(true);
        frontLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(frontLabel, BorderLayout.EAST);

        setVisible(true);
    }

    private void drawQueue(Graphics g) {
        int size = queue.size();
        if (size == 0) return;

        int boxWidth = Math.min(100, (queuePanel.getWidth() - 40) / size);
        int x = 20;
        int y = queuePanel.getHeight() / 2 - 30;

        int index = 0;
        for (int value : queue) {
            g.setColor(new Color(60, 120, 200));
            g.fillRect(x, y, boxWidth - 10, 60);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, boxWidth - 10, 60);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.drawString(String.valueOf(value), x + 15, y + 35);

            x += boxWidth;
            index++;
        }
    }

    private void updateFrontLabel() {
        SwingUtilities.invokeLater(() -> {
            if (queue.isEmpty()) {
                frontLabel.setText("Front: [empty]");
            } else {
                frontLabel.setText("Front: " + queue.peek());
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
