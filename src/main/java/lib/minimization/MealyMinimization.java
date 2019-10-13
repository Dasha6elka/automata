package lib.minimization;

import lib.models.MealyEdge;
import lib.models.MealyNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MealyMinimization {
    private char nextLetterKey = 'A';

    public List<MealyEdge> minimizeGraph(List<MealyEdge> mooreEdges) {
        var sortMooreEdges = sortEdges(mooreEdges);
        HashSet<MealyNode> reachableNodes = new HashSet<>();

        for (MealyEdge v : sortMooreEdges) {
            if (reachableNodes.isEmpty()) {
                reachableNodes.add(v.from);
                reachableNodes.add(v.to);
            } else {
                if (reachableNodes.contains(v.from)) {
                    reachableNodes.add(v.to);
                }
            }
        }

        Map<MealyNode, List<MealyEdge>> table = new HashMap<>();
        reachableNodes.forEach(mooreNode -> {
            var reachableEdges = mooreEdges
                .stream()
                .filter(mooreEdge -> mooreEdge.from.equals(mooreNode))
                .collect(Collectors.toList());
            table.put(mooreNode, reachableEdges);
        });
        Map<List<String>, String> groupedByY = new HashMap<>();
        Map<String, List<MealyNode>> groupedByKey = new HashMap<>();
        AtomicInteger index = new AtomicInteger();

        table
            .forEach((tableKey, tableValue) -> {
                String value;
                List<String> key = tableValue
                    .stream()
                    .map(mealyEdge -> mealyEdge.y)
                    .collect(Collectors.toList());
                if (!groupedByY.containsKey(key)) {
                    value = String.valueOf(nextLetterKey) + index.getAndIncrement();
                } else {
                    value = groupedByY.get(key);
                }
                groupedByY.put(key, value);
                if (!groupedByKey.containsKey(value)) {
                    ArrayList<MealyNode> list = new ArrayList<>();
                    list.add(tableKey);
                    groupedByKey.put(value, list);
                } else {
                    var list = groupedByKey.get(value);
                    list.add(tableKey);
                    groupedByKey.put(value, list);
                }
            });

        int currSize = groupedByKey.size();
        int prevSize = 0;
        while (prevSize != currSize) {
            prevSize = currSize;
            currSize = groupKeysDeep(table, groupedByKey);
        }

        List<MealyEdge> resultMooreEdges = new ArrayList<>();
        table.forEach((mooreNode, mooreNodes) -> {
            index.set(0);
            mooreNodes.forEach(childMooreEdge -> {
                MealyEdge mooreEdge = new MealyEdge();
                mooreEdge.from = mooreNode;
                mooreEdge.to = childMooreEdge.to;
                mooreEdge.x = "x" + index.getAndIncrement();
                mooreEdge.y = childMooreEdge.y;

                resultMooreEdges.add(mooreEdge);
            });
        });

        return resultMooreEdges;
    }

    private int groupKeysDeep(Map<MealyNode, List<MealyEdge>> table,
                              Map<String, List<MealyNode>> groupedByKey) {
        nextLetterKey++;
        groupedByKey.clear();
        AtomicInteger index = new AtomicInteger();

        table
            .keySet()
            .forEach(mooreNode -> {
                String value = String.valueOf(nextLetterKey) + index.getAndIncrement();
                if (!groupedByKey.containsKey(value)) {
                    ArrayList<MealyNode> list = new ArrayList<>();
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
                    mooreNodes.forEach(cellEdge -> {
                        if (cellEdge.equals(mooreNode)) {
                            cellEdge.to.q = key;
                        }
                    });
                });
            });
        });

        return groupedByKey.size();
    }

    private List<MealyEdge> sortEdges(List<MealyEdge> mooreEdges) {
        return mooreEdges.stream().sorted((left, right) -> {
            int a = Integer.parseInt(left.to.q.substring(1));
            int b = Integer.parseInt(right.to.q.substring(1));
            if (a > b) {
                return 1;
            } else if (a < b) {
                return -1;
            } else {
                int c = Integer.parseInt(left.y.substring(1));
                int d = Integer.parseInt(right.y.substring(1));
                if (c > d) {
                    return 1;
                } else if (c < d) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }).collect(Collectors.toList());
    }
}
