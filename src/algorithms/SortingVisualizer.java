package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SortingVisualizer extends JFrame {

    private final List<Integer> bars = new ArrayList<>();
    private final Map<Integer, Color> barColors = new HashMap<>();
    private final Set<Integer> sortedIndices = new HashSet<>();

    private final Color DEFAULT_COLOR = new Color(70, 130, 180);
    private final Color COMPARING_COLOR = Color.YELLOW;
    private final Color SWAPPING_COLOR = Color.RED;
    private final Color SORTED_COLOR = new Color(0, 200, 100);

    private JPanel drawPanel;
    private JLabel arrayStatusLabel;
    private JTextArea explanationArea;
    private JComboBox<String> algorithmCombo;

    private volatile boolean sorting = false;

    public SortingVisualizer() {
        setTitle("Sorting Visualizer");
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

        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        explanationArea.setBackground(new Color(40, 40, 40));
        explanationArea.setForeground(Color.LIGHT_GRAY);
        explanationArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        explanationArea.setText(getExplanation("Bubble Sort"));

        JPanel colorLegendPanel = createColorLegend();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.add(arrayStatusLabel, BorderLayout.NORTH);
        topPanel.add(colorLegendPanel, BorderLayout.CENTER);
        topPanel.add(explanationArea, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

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

        algorithmCombo = new JComboBox<>(new String[]{"Bubble Sort", "Merge Sort", "Quick Sort"});
        algorithmCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        algorithmCombo.addActionListener(e -> {
            String selected = (String) algorithmCombo.getSelectedItem();
            explanationArea.setText(getExplanation(selected));
        });

        JButton addBarBtn = createStyledButton("Add Bar", () -> {
            if (sorting) {
                showError("Cannot add bars while sorting.");
                return;
            }
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

        JButton sortBtn = createStyledButton("Sort", () -> {
            if (sorting) {
                showError("Sorting is already in progress.");
                return;
            }
            sorting = true;
            String selected = (String) algorithmCombo.getSelectedItem();
            new Thread(() -> {
                try {
                    switch (selected) {
                        case "Bubble Sort" -> bubbleSort();
                        case "Merge Sort" -> mergeSort(0, bars.size() - 1);
                        case "Quick Sort" -> quickSort(0, bars.size() - 1);
                    }
                    for (int i = 0; i < bars.size(); i++) setBarState(i, "sorted");
                    repaint();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    sorting = false;
                }
            }).start();
        });

        JButton resetBtn = createStyledButton("Reset", () -> {
            sorting = false;
            bars.clear();
            barColors.clear();
            sortedIndices.clear();
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
        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmCombo);
        controlPanel.add(sortBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(backBtn);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createColorLegend() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setBackground(new Color(30, 30, 30));

        legendPanel.add(createColorItem(DEFAULT_COLOR, "Default"));
        legendPanel.add(createColorItem(COMPARING_COLOR, "Comparing"));
        legendPanel.add(createColorItem(SWAPPING_COLOR, "Swapping"));
        legendPanel.add(createColorItem(SORTED_COLOR, "Sorted"));

        return legendPanel;
    }

    private JPanel createColorItem(Color color, String label) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel textLabel = new JLabel(label);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(colorBox);
        panel.add(textLabel);
        return panel;
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
        if (!sorting || sortedIndices.contains(index)) return;
        switch (state) {
            case "comparing" -> barColors.put(index, COMPARING_COLOR);
            case "swapping" -> barColors.put(index, SWAPPING_COLOR);
            case "sorted" -> {
                barColors.put(index, SORTED_COLOR);
                sortedIndices.add(index);
            }
            default -> barColors.put(index, DEFAULT_COLOR);
        }
    }

    private void bubbleSort() throws InterruptedException {
        for (int i = 0; i < bars.size() - 1; i++) {
            for (int j = 0; j < bars.size() - i - 1; j++) {
                if (!sorting) return;
                setBarState(j, "comparing");
                setBarState(j + 1, "comparing");
                repaint();
                Thread.sleep(100);
                if (bars.get(j) > bars.get(j + 1)) {
                    setBarState(j, "swapping");
                    setBarState(j + 1, "swapping");
                    Collections.swap(bars, j, j + 1);
                    updateArrayLabel();
                    repaint();
                    Thread.sleep(150);
                }
                setBarState(j, "default");
                setBarState(j + 1, "default");
            }
            setBarState(bars.size() - i - 1, "sorted");
        }
        setBarState(0, "sorted");
    }

    private void mergeSort(int left, int right) throws InterruptedException {
        if (!sorting || left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(left, mid);
        mergeSort(mid + 1, right);
        merge(left, mid, right);
    }

    private void merge(int left, int mid, int right) throws InterruptedException {
        List<Integer> temp = new ArrayList<>();
        int i = left, j = mid + 1;

        while (i <= mid && j <= right) {
            if (!sorting) return;
            setBarState(i, "comparing");
            setBarState(j, "comparing");
            repaint();
            Thread.sleep(100);

            if (bars.get(i) <= bars.get(j)) {
                temp.add(bars.get(i++));
            } else {
                temp.add(bars.get(j++));
            }
        }

        while (i <= mid) temp.add(bars.get(i++));
        while (j <= right) temp.add(bars.get(j++));

        for (int k = 0; k < temp.size(); k++) {
            bars.set(left + k, temp.get(k));
            updateArrayLabel();
            repaint();
            Thread.sleep(50);
        }

        for (int l = left; l <= right; l++) setBarState(l, "sorted");
    }

    private void quickSort(int low, int high) throws InterruptedException {
        if (!sorting || low >= high) return;
        int pi = partition(low, high);
        quickSort(low, pi - 1);
        quickSort(pi + 1, high);
    }

    private int partition(int low, int high) throws InterruptedException {
        int pivot = bars.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (!sorting || sortedIndices.contains(j)) continue;
            setBarState(j, "comparing");
            repaint();
            Thread.sleep(100);
            if (bars.get(j) < pivot) {
                i++;
                setBarState(i, "swapping");
                setBarState(j, "swapping");
                Collections.swap(bars, i, j);
                repaint();
                Thread.sleep(150);
            }
            setBarState(j, "default");
        }
        Collections.swap(bars, i + 1, high);
        updateArrayLabel();
        Thread.sleep(150);
        setBarState(i + 1, "sorted");
        return i + 1;
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
            case "Bubble Sort" -> "Bubble Sort: Repeatedly compares and swaps adjacent elements if they are in the wrong order.";
            case "Merge Sort" -> "Merge Sort: Recursively divides the array and merges sorted halves.";
            case "Quick Sort" -> "Quick Sort: Selects a pivot and partitions the array around it.";
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

}
