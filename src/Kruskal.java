import java.util.*;

public class Kruskal {
    public static class Result {
        public List<Edge> mstEdges = new ArrayList<>();
        public int totalCost = 0;
        public int operations = 0;
        public double timeMs;
    }

    public static Result run(List<String> nodes, List<Edge> edges) {
        Result res = new Result();
        long start = System.nanoTime();

        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingInt(e -> e.weight));
        res.operations += edges.size();

        UnionFind uf = new UnionFind(nodes);

        for (Edge e : sorted) {
            res.operations++;
            if (uf.union(e.from, e.to)) {
                res.mstEdges.add(e);
                res.totalCost += e.weight;
            }
            if (res.mstEdges.size() == nodes.size() - 1) break;
        }

        res.operations += uf.ops;
        res.timeMs = (System.nanoTime() - start) / 1000000.0;
        return res;
    }
}
