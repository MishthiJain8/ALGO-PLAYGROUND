import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.*;

// Main public class containing GUI + runner
public class AlgoPlayground {
    private JFrame frame;
    private JComboBox<String> algoSelect;
    private JTextArea inputArea;
    private JButton runButton;
    private JTextArea outputArea;
    private JLabel instructionLabel;

    public AlgoPlayground() {
        initUI();
    }

    private void initUI() {
        frame = new JFrame("AlgoPlayground - Minimal DSA Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 560);
        frame.setLocationRelativeTo(null);

        // top panel: algorithm selection
        JPanel top = new JPanel(new BorderLayout(8, 8));
        String[] algos = {
            "Bubble Sort",
            "Merge Sort",
            "Binary Search",
            "Graph BFS",
            "Graph DFS"
        };
        algoSelect = new JComboBox<>(algos);
        top.add(new JLabel("Choose algorithm:"), BorderLayout.WEST);
        top.add(algoSelect, BorderLayout.CENTER);

        // center: input / output split
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 8));

        // left: input
        JPanel left = new JPanel(new BorderLayout(6,6));
        instructionLabel = new JLabel(getInstructionFor((String)algoSelect.getSelectedItem()));
        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        left.add(instructionLabel, BorderLayout.NORTH);
        left.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        // right: output
        JPanel right = new JPanel(new BorderLayout(6,6));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        right.add(new JLabel("Algorithm Steps / Output:"), BorderLayout.NORTH);
        right.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        center.add(left);
        center.add(right);

        // bottom: run button
        runButton = new JButton("Run");
        JPanel bottom = new JPanel();
        bottom.add(runButton);

        frame.getContentPane().add(top, BorderLayout.NORTH);
        frame.getContentPane().add(center, BorderLayout.CENTER);
        frame.getContentPane().add(bottom, BorderLayout.SOUTH);

        // Listeners
        algoSelect.addActionListener(e -> instructionLabel.setText(getInstructionFor((String)algoSelect.getSelectedItem())));
        runButton.addActionListener(e -> runSelectedAlgorithm());

        frame.setVisible(true);
    }

    private String getInstructionFor(String algo) {
        switch (algo) {
            case "Bubble Sort":
            case "Merge Sort":
            case "Binary Search":
                return "<html>Input format: comma-separated integers. Example:<br>5,3,8,1,2<br>For Binary Search: second line = target e.g.<br>5,3,8,1,2\\n8</html>";
            case "Graph BFS":
            case "Graph DFS":
                return "<html>Graph input format:<br>First line: n (number of nodes, nodes are 0..n-1)<br>Next lines: edges as u v per line<br>Blank line to end edges<br>Then start node on final line.<br>Example:<br>5<br>0 1<br>0 2<br>1 3<br>3 4<br><br>0</html>";
            default:
                return "Enter input as comma-separated integers";
        }
    }

    private void runSelectedAlgorithm() {
        String algo = (String) algoSelect.getSelectedItem();
        String input = inputArea.getText().trim();
        outputArea.setText("");
        if (input.isEmpty()) {
            outputArea.setText("Please provide input in the left pane as per instruction.");
            return;
        }

        switch (algo) {
            case "Bubble Sort":
                runBubble(input);
                break;
            case "Merge Sort":
                runMerge(input);
                break;
            case "Binary Search":
                runBinarySearch(input);
                break;
            case "Graph BFS":
                runGraphBFS(input);
                break;
            case "Graph DFS":
                runGraphDFS(input);
                break;
            default:
                outputArea.setText("Algorithm not implemented.");
        }
    }

    // ---------- Helpers to parse arrays ----------
    private int[] parseArray(String s) {
        s = s.replaceAll("\\s+", "");
        String[] parts = s.split(",");
        ArrayList<Integer> nums = new ArrayList<>();
        for (String p: parts) {
            if (p.length()==0) continue;
            try {
                nums.add(Integer.parseInt(p));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        int[] a = new int[nums.size()];
        for (int i=0;i<nums.size();i++) a[i] = nums.get(i);
        return a;
    }

    private void runBubble(String input) {
        int[] a = parseArray(input);
        if (a.length==0) { outputArea.setText("Parsed zero numbers."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("Original array: ").append(Arrays.toString(a)).append("\n\n");
        Algorithms.bubbleSortWithSteps(a, sb);
        outputArea.setText(sb.toString());
    }

    private void runMerge(String input) {
        int[] a = parseArray(input);
        if (a.length==0) { outputArea.setText("Parsed zero numbers."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("Original array: ").append(Arrays.toString(a)).append("\n\n");
        Algorithms.mergeSortWithSteps(a, sb);
        outputArea.setText(sb.toString());
    }

    private void runBinarySearch(String input) {
        String[] lines = input.split("\\r?\\n");
        if (lines.length==0) { outputArea.setText("Provide array and target (on new line)."); return; }
        int[] a = parseArray(lines[0]);
        if (a.length==0) { outputArea.setText("Parsed zero numbers."); return; }
        if (lines.length < 2) { outputArea.setText("Provide target on second line."); return; }
        int target;
        try { target = Integer.parseInt(lines[1].trim()); } catch (Exception e) { outputArea.setText("Invalid target."); return; }
        Arrays.sort(a);
        StringBuilder sb = new StringBuilder();
        sb.append("Sorted array (binary search requires sorted): ").append(Arrays.toString(a)).append("\n\n");
        int idx = Algorithms.binarySearchWithSteps(a, target, sb);
        sb.append("\nResult: target ").append(target).append(idx>=0?(" found at index "+idx):" not found");
        outputArea.setText(sb.toString());
    }

    private void runGraphBFS(String input) {
        GraphInput gi = parseGraphInput(input);
        if (gi==null) { outputArea.setText("Invalid graph input. See instruction."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("Graph with n=").append(gi.n).append("\nEdges:\n");
        for (int u=0; u<gi.n; u++) {
            sb.append(u).append(": ").append(gi.adj.get(u)).append("\n");
        }
        sb.append("\nBFS from ").append(gi.start).append("\n\n");
        Algorithms.bfsWithSteps(gi.adj, gi.start, sb);
        outputArea.setText(sb.toString());
    }

    private void runGraphDFS(String input) {
        GraphInput gi = parseGraphInput(input);
        if (gi==null) { outputArea.setText("Invalid graph input. See instruction."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("Graph with n=").append(gi.n).append("\nEdges:\n");
        for (int u=0; u<gi.n; u++) {
            sb.append(u).append(": ").append(gi.adj.get(u)).append("\n");
        }
        sb.append("\nDFS from ").append(gi.start).append("\n\n");
        Algorithms.dfsWithSteps(gi.adj, gi.start, sb);
        outputArea.setText(sb.toString());
    }

    // Graph input parser
    private GraphInput parseGraphInput(String input) {
        try {
            String[] lines = input.split("\\r?\\n");
            if (lines.length < 2) return null;
            int idx = 0;
            while (idx < lines.length && lines[idx].trim().isEmpty()) idx++;
            if (idx >= lines.length) return null;
            int n = Integer.parseInt(lines[idx].trim());
            idx++;
            java.util.List<java.util.List<Integer>> adj = new ArrayList<>();
            for (int i=0;i<n;i++) adj.add(new ArrayList<>());

            while (idx < lines.length && !lines[idx].trim().isEmpty()) {
                String l = lines[idx].trim();
                String[] parts = l.split("\\s+");
                if (parts.length==1 && idx>0 && !lines[idx-1].trim().isEmpty() && parts[0].matches("\\d+")) {
                    break;
                }
                if (parts.length >= 2) {
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);
                    if (u>=0 && u<n && v>=0 && v<n) {
                        adj.get(u).add(v);
                        adj.get(v).add(u); // undirected
                    }
                }
                idx++;
            }

            while (idx < lines.length && lines[idx].trim().isEmpty()) idx++;
            int start = 0;
            if (idx < lines.length) start = Integer.parseInt(lines[idx].trim());
            return new GraphInput(n, adj, start);
        } catch (Exception e) {
            return null;
        }
    }

    // helper graph input holder
    private static class GraphInput {
        int n;
        java.util.List<java.util.List<Integer>> adj;
        int start;
        GraphInput(int n, java.util.List<java.util.List<Integer>> adj, int start) {
            this.n = n; this.adj = adj; this.start = start;
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AlgoPlayground());
    }
}

// Separate class for algorithms and step-by-step tracing
class Algorithms {

    public static void bubbleSortWithSteps(int[] a, StringBuilder sb) {
        int n = a.length;
        boolean swapped;
        for (int pass = 1; pass <= n-1; pass++) {
            swapped = false;
            sb.append("Pass ").append(pass).append(": ");
            for (int i=0;i<n-pass;i++) {
                sb.append("compare ").append(a[i]).append(" and ").append(a[i+1]).append("; ");
                if (a[i] > a[i+1]) {
                    int tmp = a[i];
                    a[i] = a[i+1];
                    a[i+1] = tmp;
                    swapped = true;
                    sb.append("swap -> ").append(Arrays.toString(a)).append(" ; ");
                }
            }
            sb.append("\n");
            if (!swapped) {
                sb.append("No swaps in this pass, array is sorted.\n");
                break;
            }
        }
        sb.append("\nFinal sorted array: ").append(Arrays.toString(a)).append("\n");
    }

    public static void mergeSortWithSteps(int[] a, StringBuilder sb) {
        sb.append("Running Merge Sort...\n");
        mergeSortRec(a, 0, a.length - 1, sb);
        sb.append("\nFinal sorted array: ").append(Arrays.toString(a)).append("\n");
    }

    private static void mergeSortRec(int[] a, int l, int r, StringBuilder sb) {
        if (l >= r) return;
        int m = (l + r) / 2;
        mergeSortRec(a, l, m, sb);
        mergeSortRec(a, m+1, r, sb);
        merge(a, l, m, r, sb);
    }

    private static void merge(int[] a, int l, int m, int r, StringBuilder sb) {
        int n1 = m - l + 1;
        int n2 = r - m;
        int[] L = new int[n1];
        int[] R = new int[n2];
        for (int i=0;i<n1;i++) L[i] = a[l + i];
        for (int j=0;j<n2;j++) R[j] = a[m + 1 + j];
        sb.append("Merging ").append(Arrays.toString(L)).append(" and ").append(Arrays.toString(R)).append("\n");
        int i=0, j=0, k=l;
        while (i<n1 && j<n2) {
            if (L[i] <= R[j]) a[k++] = L[i++];
            else a[k++] = R[j++];
        }
        while (i<n1) a[k++] = L[i++];
        while (j<n2) a[k++] = R[j++];
        sb.append("After merge -> ").append(Arrays.toString(Arrays.copyOfRange(a, l, r+1))).append("\n");
    }

    public static int binarySearchWithSteps(int[] a, int target, StringBuilder sb) {
        int l = 0, r = a.length - 1;
        while (l <= r) {
            int mid = l + (r - l)/2;
            sb.append("Check indices [").append(l).append(",").append(r).append("], mid=").append(mid)
                .append(", a[mid]=").append(a[mid]).append("\n");
            if (a[mid] == target) {
                sb.append("Found target at index ").append(mid).append("\n");
                return mid;
            } else if (a[mid] < target) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        sb.append("Target not present in array.\n");
        return -1;
    }

    public static void bfsWithSteps(java.util.List<java.util.List<Integer>> adj, int start, StringBuilder sb) {
        int n = adj.size();
        boolean[] visited = new boolean[n];
        Queue<Integer> q = new LinkedList<>();
        q.add(start);
        visited[start] = true;
        sb.append("Enqueue ").append(start).append("\n");
        while (!q.isEmpty()) {
            int u = q.poll();
            sb.append("Dequeue ").append(u).append("\n");
            sb.append("Visiting ").append(u).append("\n");
            for (int v: adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    q.add(v);
                    sb.append(" -> enqueue ").append(v).append("\n");
                } else {
                    sb.append(" -> ").append(v).append(" already visited\n");
                }
            }
            sb.append("\n");
        }
        sb.append("BFS complete.\n");
    }

    public static void dfsWithSteps(java.util.List<java.util.List<Integer>> adj, int start, StringBuilder sb) {
        int n = adj.size();
        boolean[] visited = new boolean[n];
        sb.append("Starting DFS (recursive simulation) from ").append(start).append("\n\n");
        dfsRec(adj, start, visited, sb);
        sb.append("\nDFS complete.\n");
    }

    private static void dfsRec(java.util.List<java.util.List<Integer>> adj, int u, boolean[] visited, StringBuilder sb) {
        visited[u] = true;
        sb.append("Visit ").append(u).append("\n");
        for (int v: adj.get(u)) {
            if (!visited[v]) {
                sb.append(" -> go to ").append(v).append("\n");
                dfsRec(adj, v, visited, sb);
            } else {
                sb.append(" -> ").append(v).append(" already visited\n");
            }
        }
    }
}
