package lib.minimization;

import lib.models.MooreEdge;
import lib.models.MooreNode;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MooreMinimization {
    public void minimize(List<MooreEdge> mooreEdges) {
        var sortedUniqueMealyEdges = sortedUniqueEdges(mooreEdges);
        HashSet<MooreNode> visited = new HashSet<>();

        for (MooreEdge v : sortedUniqueMealyEdges) {
            if (visited.isEmpty()) {
                visited.add(v.from);
                visited.add(v.to);
            } else {
                if (visited.contains(v.from)) {
                    visited.add(v.to);
                }
            }
        }
    }

    private List<MooreEdge> sortedUniqueEdges(List<MooreEdge> uniqueMealyEdges) {
        return uniqueMealyEdges.stream().sorted((left, right) -> {
            int a = Integer.parseInt(left.from.q.substring(1));
            int b = Integer.parseInt(right.from.q.substring(1));
            return Integer.compare(a, b);
        }).collect(Collectors.toList());
    }
}
