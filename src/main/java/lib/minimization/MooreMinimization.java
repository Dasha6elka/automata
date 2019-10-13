package lib.minimization;

import lib.models.MooreEdge;
import lib.models.MooreNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MooreMinimization {
    private char nextLetterKey = 'A';

    public List<MooreEdge> minimizeGraph(List<MooreEdge> mooreEdges) {
        var sortMooreEdges = sortEdges(mooreEdges);
        HashSet<MooreNode> reachableNodes = new HashSet<>();

        for (MooreEdge v : sortMooreEdges) {
            if (reachableNodes.isEmpty()) {
                reachableNodes.add(v.from);
                reachableNodes.add(v.to);
            } else {
                if (reachableNodes.contains(v.from)) {
                    reachableNodes.add(v.to);
                }
            }
        }

        Map<MooreNode, List<MooreNode>> table = new HashMap<>();
        reachableNodes.forEach(mooreNode -> {
            var reachableEdges = mooreEdges
                .stream()
                .filter(mooreEdge -> mooreEdge.from.equals(mooreNode))
                .map(mooreEdge -> mooreEdge.to)
                .collect(Collectors.toList());
            table.put(mooreNode, reachableEdges);
        });

        Map<String, String> groupedByY = new HashMap<>();
        Map<String, List<MooreNode>> groupedByKey = new HashMap<>();
        AtomicInteger index = new AtomicInteger();

        table
            .keySet()
            .forEach(mooreNode -> {
                String value;
                if (!groupedByY.containsKey(mooreNode.y)) {
                    value = String.valueOf(nextLetterKey) + index.getAndIncrement();
                } else {
                    value = groupedByY.get(mooreNode.y);
                }
                groupedByY.put(mooreNode.y, value);
                if (!groupedByKey.containsKey(value)) {
                    ArrayList<MooreNode> list = new ArrayList<>();
                    list.add(mooreNode);
                    groupedByKey.put(value, list);
                } else {
                    var list = groupedByKey.get(value);
                    list.add(mooreNode);
                    groupedByKey.put(value, list);
                }
            });

        int currSize = groupedByKey.size();
        int prevSize = 0;
        while (prevSize != currSize) {
            prevSize = currSize;
            currSize = groupKeysDeep(table, groupedByKey);
        }

        List<MooreEdge> resultMooreEdges = new ArrayList<>();
        table.forEach((mooreNode, mooreNodes) -> {
            index.set(0);
            mooreNodes.forEach(childMooreNode -> {
                MooreEdge mooreEdge = new MooreEdge();
                mooreEdge.from = mooreNode;
                mooreEdge.to = childMooreNode;
                mooreEdge.x = "x" + index.getAndIncrement();

                resultMooreEdges.add(mooreEdge);
            });
        });

        return resultMooreEdges;
    }

    private int groupKeysDeep(Map<MooreNode, List<MooreNode>> table,
                              Map<String, List<MooreNode>> groupedByKey) {
        nextLetterKey++;
        groupedByKey.clear();
        AtomicInteger index = new AtomicInteger();

        table
            .keySet()
            .forEach(mooreNode -> {
                String value = String.valueOf(nextLetterKey) + index.getAndIncrement();
                if (!groupedByKey.containsKey(value)) {
                    ArrayList<MooreNode> list = new ArrayList<>();
                    list.add(mooreNode);
                    groupedByKey.put(value, list);
                } else {
                    var list = groupedByKey.get(value);
                    list.add(mooreNode);
                    groupedByKey.put(value, list);
                }
            });

        groupedByKey.forEach((key, nodes) -> {
            nodes.forEach(mooreNode -> {
                table.forEach((tableNode, mooreNodes) -> {
                    mooreNodes.forEach(cellNode -> {
                        if (cellNode.equals(mooreNode)) {
                            cellNode.q = key;
                        }
                    });
                });
            });
        });

        return groupedByKey.size();
    }

    private List<MooreEdge> sortEdges(List<MooreEdge> mooreEdges) {
        return mooreEdges.stream().sorted((left, right) -> {
            int a = Integer.parseInt(left.from.q.substring(1));
            int b = Integer.parseInt(right.from.q.substring(1));
            return Integer.compare(a, b);
        }).collect(Collectors.toList());
    }
}
