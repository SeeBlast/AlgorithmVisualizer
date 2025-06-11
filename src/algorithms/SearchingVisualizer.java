package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SearchingVisualizer extends JFrame {

    private final List<Integer> bars = new ArrayList<>();
    private final Map<Integer, Color> barColors = new HashMap<>();

    private final Color DEFAULT_COLOR = new Color(70, 130, 180);
    private final Color COMPARING_COLOR = Color.YELLOW;
    private final Color FOUND_COLOR = new Color(0, 200, 100);

    private JPanel drawPanel;
    private JLabel arrayStatusLabel;
    private JTextArea explanationArea;
    private JComboBox<String> algorithmCombo;

    public SearchingVisualizer() {
        setTitle("Algorithm Visualizer");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        generateInitialBars(20);

        arrayStatusLabel = new JLabel("Array: " + barsToString());
        arrayStatusLabel.setForeground(Color.WHITE);
        arrayStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        arrayStatusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        arrayStatusLabel.setBackground(new Color(30, 30, 30));
        arrayStatusLabel.setOpaque(true);
        arrayStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        explanationArea.setBackground(new Color(40, 40, 40));
        explanationArea.setForeground(Color.LIGHT_GRAY);
        explanationArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        explanationArea.setText(getExplanation("Linear Search"));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        legendPanel.setBackground(new Color(30, 30, 30));
        legendPanel.add(createColorLegend(DEFAULT_COLOR, "Default"));
        legendPanel.add(createColorLegend(COMPARING_COLOR, "Comparing"));
        legendPanel.add(createColorLegend(FOUND_COLOR, "Found"));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.add(arrayStatusLabel);
        topPanel.add(legendPanel);
        topPanel.add(explanationArea);

        add(topPanel, BorderLayout.NORTH);
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        explanationArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        drawPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBars(g);
            }
        };
        drawPanel.setBackground(new Color(20, 20, 20));
        add(drawPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(30, 30, 30));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField searchField = new JTextField(5);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        algorithmCombo = new JComboBox<>(new String[]{"Linear Search", "Binary Search"});
        algorithmCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        algorithmCombo.addActionListener(e -> {
            String selected = (String) algorithmCombo.getSelectedItem();
            explanationArea.setText(getExplanation(selected));
        });

        JButton addBarBtn = createStyledButton("Add Bar", () -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                if (value > 0 && value <= 100) {
                    bars.add(value);
                    inputField.setText("");
                    updateArrayLabel();
                    repaint();
                } else {
                    showError("Please enter a value between 1 and 100.");
                }
            } catch (NumberFormatException e) {
                showError("Invalid input. Please enter a number.");
            }
        });

        JButton searchBtn = createStyledButton("Search", () -> {
            try {
                int target = Integer.parseInt(searchField.getText());
                String selected = (String) algorithmCombo.getSelectedItem();
                new Thread(() -> {
                    try {
                        switch (selected) {
                            case "Linear Search" -> linearSearch(target);
                            case "Binary Search" -> binarySearch(target);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } catch (NumberFormatException e) {
                showError("Invalid input. Please enter a number.");
            }
        });

        JButton resetBtn = createStyledButton("Reset", () -> {
            bars.clear();
            barColors.clear();
            generateInitialBars(20);
            updateArrayLabel();
            repaint();
        });

        JButton backBtn = createStyledButton("Back to Menu", () -> {
            this.dispose();
            new ui.MainMenu();
        });


        controlPanel.add(new JLabel("Bar Height (1–100):"));
        controlPanel.add(inputField);
        controlPanel.add(addBarBtn);
        controlPanel.add(new JLabel("Search for:"));
        controlPanel.add(searchField);
        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmCombo);
        controlPanel.add(searchBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(backBtn);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void drawBars(Graphics g) {
        if (bars.isEmpty()) return;

        int width = drawPanel.getWidth() / bars.size();
        for (int i = 0; i < bars.size(); i++) {
            int height = bars.get(i) * 3;
            int x = i * width + 2;
            int y = drawPanel.getHeight() - height;

            Color color = barColors.getOrDefault(i, DEFAULT_COLOR);
            g.setColor(color);
            g.fillRect(x, y, width - 4, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width - 4, height);
        }
    }

    private void setBarState(int index, String state) {
        switch (state) {
            case "comparing" -> barColors.put(index, COMPARING_COLOR);
            case "found" -> barColors.put(index, FOUND_COLOR);
            default -> barColors.put(index, DEFAULT_COLOR);
        }
    }

    private void linearSearch(int target) throws InterruptedException {
        for (int i = 0; i < bars.size(); i++) {
            setBarState(i, "comparing");
            repaint();
            Thread.sleep(200);
            if (bars.get(i) == target) {
                setBarState(i, "found");
                repaint();
                return;
            }
            setBarState(i, "default");
        }
        repaint();
    }

    private void binarySearch(int target) throws InterruptedException {
        Collections.sort(bars);
        barColors.clear();  // Reset colors after sort
        updateArrayLabel();
        repaint();
        int left = 0, right = bars.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            setBarState(mid, "comparing");
            repaint();
            Thread.sleep(300);

            if (bars.get(mid) == target) {
                setBarState(mid, "found");
                repaint();
                return;
            } else {
                setBarState(mid, "default");
                if (bars.get(mid) < target) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
    }

    private void generateInitialBars(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            bars.add(rand.nextInt(100) + 1);
        }
    }

    private void updateArrayLabel() {
        SwingUtilities.invokeLater(() -> arrayStatusLabel.setText("Array: " + barsToString()));
    }

    private String barsToString() {
        return bars.toString();
    }

    private String getExplanation(String algorithm) {
        return switch (algorithm) {
            case "Linear Search" -> "Linear Search: Traverse each element until target is found.";
            case "Binary Search" -> "Binary Search: Divide and conquer. Requires sorted array.";
            default -> "";
        };
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

    private JPanel createColorLegend(Color color, String label) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(new Color(30, 30, 30));

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(16, 16));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JLabel text = new JLabel(label);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panel.add(colorBox);
        panel.add(text);
        return panel;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

}
