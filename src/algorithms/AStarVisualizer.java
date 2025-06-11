package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AStarVisualizer extends JFrame {

    private final int NODE_COUNT = 10;
    private final int PANEL_WIDTH = 800;
    private final int PANEL_HEIGHT = 500;

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Random rand = new Random();

    private JComboBox<String> startSelector;
    private JComboBox<String> goalSelector;
    private JTextArea logArea;
    private JPanel graphPanel;

    public AStarVisualizer() {
        setTitle("A* Algorithm Visualizer");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("A* Algorithm Visualization", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // LEFT PANEL - Logs
        logArea = new JTextArea(20, 25);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Steps"));
        add(scroll, BorderLayout.WEST);

        // CONTROL PANEL
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(30, 30, 30));

        startSelector = new JComboBox<>();
        goalSelector = new JComboBox<>();

        JButton runBtn = new JButton("Run A*");
        JButton resetBtn = new JButton("Reset Graph");
        JButton backBtn = new JButton("Back to Menu");

        runBtn.addActionListener(e -> runAStar());
        resetBtn.addActionListener(e -> {
            generateGraph();
            graphPanel.repaint();
            updateSelectors();
            logArea.setText("");
        });
        backBtn.addActionListener(e -> {
            this.dispose();
            new ui.AdvancedAlgorithmsVisualizer();
        });

        controlPanel.add(new JLabel("Start:"));
        controlPanel.add(startSelector);
        controlPanel.add(new JLabel("Goal:"));
        controlPanel.add(goalSelector);
        controlPanel.add(runBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(backBtn);

        add(controlPanel, BorderLayout.SOUTH);

        // GRAPH PANEL
        graphPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph((Graphics2D) g);
            }
        };
        graphPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        graphPanel.setBackground(new Color(20, 20, 20));
        add(graphPanel, BorderLayout.CENTER);

        generateGraph();
        updateSelectors();
        setVisible(true);
    }

    private void generateGraph() {
        nodes.clear();
        edges.clear();

        for (int i = 0; i < NODE_COUNT; i++) {
            int x = rand.nextInt(PANEL_WIDTH - 100) + 50;
            int y = rand.nextInt(PANEL_HEIGHT - 100) + 50;
            nodes.add(new Node(i, x, y));
        }

        for (int i = 0; i < NODE_COUNT; i++) {
            for (int j = i + 1; j < NODE_COUNT; j++) {
                if (rand.nextDouble() < 0.3) {
                    int w = rand.nextInt(15) + 1;
                    edges.add(new Edge(nodes.get(i), nodes.get(j), w));
                    edges.add(new Edge(nodes.get(j), nodes.get(i), w));
                }
            }
        }
    }

    private void updateSelectors() {
        startSelector.removeAllItems();
        goalSelector.removeAllItems();
        for (Node node : nodes) {
            startSelector.addItem(String.valueOf(node.id));
            goalSelector.addItem(String.valueOf(node.id));
        }
    }

    private void runAStar() {
        int startId = Integer.parseInt((String) startSelector.getSelectedItem());
        int goalId = Integer.parseInt((String) goalSelector.getSelectedItem());
        logArea.setText("");

        Map<Integer, Integer> cameFrom = new HashMap<>();
        Map<Integer, Integer> gScore = new HashMap<>();
        Map<Integer, Integer> fScore = new HashMap<>();

        for (Node node : nodes) {
            gScore.put(node.id, Integer.MAX_VALUE);
            fScore.put(node.id, Integer.MAX_VALUE);
        }
        gScore.put(startId, 0);
        fScore.put(startId, heuristic(startId, goalId));

        PriorityQueue<NodeCost> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        openSet.add(new NodeCost(startId, fScore.get(startId)));

        Set<Integer> visited = new HashSet<>();

        while (!openSet.isEmpty()) {
            NodeCost current = openSet.poll();
            if (!visited.add(current.id)) continue;

            log("Visiting node " + current.id);
            highlightNode(current.id, Color.YELLOW);
            if (current.id == goalId) {
                reconstructPath(cameFrom, goalId);
                return;
            }

            for (Edge edge : edges) {
                if (edge.start.id == current.id) {
                    int neighbor = edge.end.id;
                    int tentativeG = gScore.get(current.id) + edge.weight;
                    if (tentativeG < gScore.get(neighbor)) {
                        cameFrom.put(neighbor, current.id);
                        gScore.put(neighbor, tentativeG);
                        int f = tentativeG + heuristic(neighbor, goalId);
                        fScore.put(neighbor, f);
                        openSet.add(new NodeCost(neighbor, f));
                        log("Updating node " + neighbor + " with f=" + f);
                    }
                }
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }

            graphPanel.repaint();
        }

        log("No path found to goal.");
    }

    private void reconstructPath(Map<Integer, Integer> cameFrom, int current) {
        List<Integer> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(current);
        Collections.reverse(path);
        log("Path found: " + path);

        new Thread(() -> {
            for (int i = 0; i < path.size(); i++) {
                highlightNode(path.get(i), Color.GREEN);
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ignored) {
                }
                graphPanel.repaint();
            }
        }).start();
    }

    private int heuristic(int a, int b) {
        Node na = nodes.get(a);
        Node nb = nodes.get(b);
        return (int) Math.hypot(na.x - nb.x, na.y - nb.y);
    }

    private void highlightNode(int id, Color color) {
        nodes.get(id).color = color;
    }

    private void drawGraph(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Edge e : edges) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(e.start.x, e.start.y, e.end.x, e.end.y);
            String label = String.valueOf(e.weight);
            int mx = (e.start.x + e.end.x) / 2;
            int my = (e.start.y + e.end.y) / 2;
            g2.drawString(label, mx, my);
        }

        for (Node n : nodes) {
            g2.setColor(n.color);
            g2.fillOval(n.x - 15, n.y - 15, 30, 30);
            g2.setColor(Color.BLACK);
            g2.drawOval(n.x - 15, n.y - 15, 30, 30);
            g2.drawString(String.valueOf(n.id), n.x - 5, n.y + 5);
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    private static class Node {
        int id, x, y;
        Color color = Color.CYAN;

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

    private static class NodeCost {
        int id, f;

        public NodeCost(int id, int f) {
            this.id = id;
            this.f = f;
        }
    }

}
