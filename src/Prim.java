import java.util.*;

public class Prim {
    public static class Result {
        public List<Edge> mstEdges = new ArrayList<>();
        public int totalCost = 0;
        public int operations = 0;
        public double timeMs;
    }

    public static Result run(List<String> nodes, List<Edge> edges) {
        Result res = new Result();
        long start = System.nanoTime();

        Map<String, List<Edge>> adj = new HashMap<>();
        for (String n : nodes) adj.put(n, new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.from).add(new Edge(e.from, e.to, e.weight));
            adj.get(e.to).add(new Edge(e.to, e.from, e.weight));
        }

        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        String startNode = nodes.get(0);
        visited.add(startNode);
        pq.addAll(adj.get(startNode));
        res.operations += adj.get(startNode).size();

        while (!pq.isEmpty() && visited.size() < nodes.size()) {
            Edge e = pq.poll();
            res.operations++;
            if (visited.contains(e.to)) continue;
            visited.add(e.to);
            res.mstEdges.add(e);
            res.totalCost += e.weight;
            for (Edge next : adj.get(e.to)) {
                pq.add(next);
                res.operations++;
            }
        }

        res.timeMs = (System.nanoTime() - start) / 1e6;
        return res;
    }
}
