import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnionFind {
    private final Map<String, String> parent = new HashMap<>();
    private final Map<String, Integer> rank = new HashMap<>();
    public int ops = 0;

    public UnionFind(List<String> nodes) {
        for (String n : nodes) {
            parent.put(n, n);
            rank.put(n, 0);
        }
    }

    public String find(String x) {
        ops++;
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x)));
        }
        return parent.get(x);
    }

    public boolean union(String a, String b) {
        ops++;
        String ra = find(a), rb = find(b);
        if (ra.equals(rb)) return false;
        if (rank.get(ra) < rank.get(rb)) parent.put(ra, rb);
        else if (rank.get(ra) > rank.get(rb)) parent.put(rb, ra);
        else {
            parent.put(rb, ra);
            rank.put(ra, rank.get(ra) + 1);
        }
        return true;
    }
}

