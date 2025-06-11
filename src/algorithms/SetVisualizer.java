package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class SetVisualizer extends JFrame {

    private final Set<Integer> set = new HashSet<>();
    private final JPanel setPanel;
    private final java.util.List<Color> colors = new java.util.ArrayList<>();

    private final Color DEFAULT_COLOR = new Color(60, 120, 200);
    private final Color EXISTS_COLOR = new Color(0, 200, 100);
    private final Color CHECKING_COLOR = Color.YELLOW;

    public SetVisualizer() {
        setTitle("Set Visualizer");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Set (No Duplicates)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        setPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSet(g);
            }
        };
        setPanel.setBackground(new Color(20, 20, 20));
        add(setPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        controlPanel.setLayout(new FlowLayout());

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton addBtn = createStyledButton("Add", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                inputField.setText("");
                if (set.contains(value)) {
                    showError("Value already exists in the set.");
                } else {
                    set.add(value);
                    colors.add(DEFAULT_COLOR);
                    repaint();
                }
            } catch (NumberFormatException e) {
                showError("Enter a valid integer");
            }
        });

        JButton checkBtn = createStyledButton("Check", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                inputField.setText("");
                new Thread(() -> check(value)).start();
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
        controlPanel.add(addBtn);
        controlPanel.add(checkBtn);
        controlPanel.add(backBtn);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void check(int value) {
        int index = 0;
        for (Integer val : set) {
            colors.set(index, CHECKING_COLOR);
            repaint();
            sleep(400);
            if (val == value) {
                colors.set(index, EXISTS_COLOR);
                repaint();
                return;
            }
            colors.set(index, DEFAULT_COLOR);
            index++;
        }
    }

    private void drawSet(Graphics g) {
        int x = 30, y = 150, width = 60, height = 40, gap = 10;
        int index = 0;
        for (Integer val : set) {
            g.setColor(colors.get(index));
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(val), x + 20, y + 25);
            x += width + gap;
            index++;
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
