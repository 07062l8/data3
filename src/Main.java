import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    static double measureMs(Runnable r, int runs) {
        for (int i = 0; i < Math.min(3, runs); i++) r.run();

        long totalNs = 0;
        for (int i = 0; i < runs; i++) {
            long s = System.nanoTime();
            r.run();
            totalNs += (System.nanoTime() - s);
        }
        return totalNs / 1_000_000.0 / runs;
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode input = (ObjectNode) mapper.readTree(new File("ass_3_input.json"));
        ArrayNode graphs = (ArrayNode) input.get("graphs");

        ArrayNode results = mapper.createArrayNode();

        for (int i = 0; i < graphs.size(); i++) {
            ObjectNode g = (ObjectNode) graphs.get(i);
            int id = g.get("id").asInt();
            List<String> nodes = new ArrayList<>();
            g.get("nodes").forEach(n -> nodes.add(n.asText()));

            List<Edge> edges = new ArrayList<>();
            g.get("edges").forEach(e -> edges.add(new Edge(
                    e.get("from").asText(),
                    e.get("to").asText(),
                    e.get("weight").asInt()
            )));

            Prim.Result primRes = Prim.run(nodes, edges);
            double primTime = measureMs(() -> Prim.run(nodes, edges), 50);
            primRes.timeMs = Double.parseDouble(String.format(Locale.US, "%.6f", primTime));

            Kruskal.Result kruskalRes = Kruskal.run(nodes, edges);
            double kruskalTime = measureMs(() -> Kruskal.run(nodes, edges), 50);
            kruskalRes.timeMs = Double.parseDouble(String.format(Locale.US, "%.6f", kruskalTime));

            ObjectNode outGraph = mapper.createObjectNode();
            outGraph.put("graph_id", id);

            ObjectNode stats = mapper.createObjectNode();
            stats.put("vertices", nodes.size());
            stats.put("edges", edges.size());
            outGraph.set("input_stats", stats);

            // Prim
            ObjectNode primNode = mapper.createObjectNode();
            ArrayNode primEdges = mapper.createArrayNode();
            for (Edge e : primRes.mstEdges) {
                ObjectNode edgeNode = mapper.createObjectNode();
                edgeNode.put("from", e.from);
                edgeNode.put("to", e.to);
                edgeNode.put("weight", e.weight);
                primEdges.add(edgeNode);
            }
            primNode.set("mst_edges", primEdges);
            primNode.put("total_cost", primRes.totalCost);
            primNode.put("operations_count", primRes.operations);
            primNode.put("execution_time_ms", primRes.timeMs);
            outGraph.set("prim", primNode);

            // Kruskal
            ObjectNode krNode = mapper.createObjectNode();
            ArrayNode krEdges = mapper.createArrayNode();
            for (Edge e : kruskalRes.mstEdges) {
                ObjectNode edgeNode = mapper.createObjectNode();
                edgeNode.put("from", e.from);
                edgeNode.put("to", e.to);
                edgeNode.put("weight", e.weight);
                krEdges.add(edgeNode);
            }
            krNode.set("mst_edges", krEdges);
            krNode.put("total_cost", kruskalRes.totalCost);
            krNode.put("operations_count", kruskalRes.operations);
            krNode.put("execution_time_ms", kruskalRes.timeMs);
            outGraph.set("kruskal", krNode);

            results.add(outGraph);
        }

        ObjectNode output = mapper.createObjectNode();
        output.set("results", results);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("ass_3_output.json"), output);

        System.out.println("Done. Results written to ass_3_output.json");
    }
}
