package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DijkstraVisualizer extends JFrame {

    private final int NODE_COUNT = 8;
    private final int PANEL_WIDTH = 600;
    private final int PANEL_HEIGHT = 500;

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Random rand = new Random();

    private JComboBox<String> sourceSelector;
    private JTextArea logArea;
    private GraphPanel graphPanel;

    private Map<Integer, Integer> finalDistances = new HashMap<>();
    private Set<String> visitedEdges = new HashSet<>();

    public DijkstraVisualizer() {
        setTitle("Dijkstra's Algorithm Visualizer");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Dijkstra's Algorithm Visualization", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(280, PANEL_HEIGHT));
        add(scrollPane, BorderLayout.WEST);

        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        add(graphPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(30, 30, 30));

        sourceSelector = new JComboBox<>();
        sourceSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton runBtn = new JButton("Run Dijkstra");
        runBtn.addActionListener(e -> runDijkstra((String) sourceSelector.getSelectedItem()));

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            generateRandomGraph();
            updateNodeSelector();
            logArea.setText("");
            finalDistances.clear();
            visitedEdges.clear();
            repaint();
        });

        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> {
            this.dispose();
            new ui.AdvancedAlgorithmsVisualizer();
        });


        bottomPanel.add(new JLabel("Start Node:"));
        bottomPanel.add(sourceSelector);
        bottomPanel.add(runBtn);
        bottomPanel.add(resetBtn);
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // Initial setup
        generateRandomGraph();
        updateNodeSelector();
        setVisible(true);
    }

    private void generateRandomGraph() {
        nodes.clear();
        edges.clear();

        for (int i = 0; i < NODE_COUNT; i++) {
            int x = rand.nextInt(PANEL_WIDTH - 100) + 50;
            int y = rand.nextInt(PANEL_HEIGHT - 100) + 50;
            nodes.add(new Node(i, x, y));
        }

        for (int i = 0; i < NODE_COUNT; i++) {
            for (int j = i + 1; j < NODE_COUNT; j++) {
                if (rand.nextDouble() < 0.35) {
                    int weight = rand.nextInt(9) + 1;
                    edges.add(new Edge(nodes.get(i), nodes.get(j), weight));
                    edges.add(new Edge(nodes.get(j), nodes.get(i), weight));
                }
            }
        }
    }

    private void updateNodeSelector() {
        sourceSelector.removeAllItems();
        for (Node node : nodes) {
            sourceSelector.addItem(String.valueOf(node.id));
        }
    }

    private void runDijkstra(String startIdStr) {
        new Thread(() -> {
            int startId = Integer.parseInt(startIdStr);
            Map<Integer, Integer> distances = new HashMap<>();
            Map<Integer, Integer> previous = new HashMap<>();
            Set<Integer> visited = new HashSet<>();
            PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

            for (Node node : nodes) distances.put(node.id, Integer.MAX_VALUE);
            distances.put(startId, 0);
            queue.add(new NodeDistance(startId, 0));

            log("Starting from node: " + startId);

            while (!queue.isEmpty()) {
                NodeDistance current = queue.poll();
                int currentId = current.nodeId;

                if (!visited.add(currentId)) continue;

                log("Visiting node " + currentId + " with distance " + current.distance);

                for (Edge edge : edges) {
                    if (edge.start.id == currentId) {
                        int neighborId = edge.end.id;
                        int newDist = distances.get(currentId) + edge.weight;

                        if (newDist < distances.get(neighborId)) {
                            distances.put(neighborId, newDist);
                            previous.put(neighborId, currentId);
                            queue.add(new NodeDistance(neighborId, newDist));
                            log("  Updated distance to node " + neighborId + " to " + newDist);
                        }
                    }
                }

                finalDistances.clear();
                finalDistances.putAll(distances);
                visitedEdges.add(String.valueOf(currentId));
                graphPanel.repaint();

                try {
                    Thread.sleep(700);
                } catch (InterruptedException ignored) {
                }
            }

            log("Dijkstra completed.");
        }).start();
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private class GraphPanel extends JPanel {
        public GraphPanel() {
            setBackground(new Color(20, 20, 20));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Edge edge : edges) {
                g2.setColor(Color.GRAY);
                int x1 = edge.start.x;
                int y1 = edge.start.y;
                int x2 = edge.end.x;
                int y2 = edge.end.y;
                g2.drawLine(x1, y1, x2, y2);

                String weightLabel = String.valueOf(edge.weight);
                int labelX = (x1 + x2) / 2;
                int labelY = (y1 + y2) / 2;
                g2.setColor(Color.WHITE);
                g2.drawString(weightLabel, labelX, labelY);
            }

            for (Node node : nodes) {
                if (finalDistances.containsKey(node.id)) {
                    g2.setColor(new Color(50, 200, 50));
                } else {
                    g2.setColor(Color.CYAN);
                }
                g2.fillOval(node.x - 15, node.y - 15, 30, 30);
                g2.setColor(Color.BLACK);
                g2.drawOval(node.x - 15, node.y - 15, 30, 30);
                g2.drawString(String.valueOf(node.id), node.x - 5, node.y + 5);
            }
        }
    }

    private static class Node {
        int id, x, y;

        public Node(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    private static class Edge {
        Node start, end;
        int weight;

        public Edge(Node start, Node end, int weight) {
            this.start = start;
            this.end = end;
            this.weight = weight;
        }
    }

    private static class NodeDistance {
        int nodeId, distance;

        public NodeDistance(int nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}
