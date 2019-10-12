package mealy_moore_converter;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.graph;

class MealyToMooreConverter {
    private String PATH_TO_OUTPUT = "output";

    private LinkSources linkSources = new LinkSources();

    List<MealyEdge> parseMealy(Scanner scanner, Integer inputsCount, Integer nodesCount) {
        ArrayList<MealyNode> mealyNodes = new ArrayList<>();
        ArrayList<MealyEdge> mealyEdges = new ArrayList<>();

        fillMealyNodes(nodesCount, mealyNodes);
        fillMealyEdges(scanner, inputsCount, nodesCount, mealyNodes, mealyEdges);

        return mealyEdges;
    }

    List<MooreEdge> printMealyToMooreGraph(List<MealyEdge> mealyEdges) {
        List<LinkSource> mealySources = linkSources.createMealyLinkSources(mealyEdges);

        Graph mealyGraph = graph("Mealy Graph")
            .directed()
            .with(mealySources);

        try {
            Graphviz
                .fromGraph(mealyGraph)
                .width(1024)
                .render(Format.PNG)
                .toFile(new File(PATH_TO_OUTPUT + "/mealy-to-more/mealy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<MooreEdge> mooreEdges = mealyToMoore(mealyEdges);
        List<LinkSource> mooreSources = linkSources.createMooreLinkSources(mooreEdges);

        Graph mooreGraph = graph("Moore Graph")
            .directed()
            .with(mooreSources);

        try {
            Graphviz
                .fromGraph(mooreGraph)
                .width(1024)
                .render(Format.PNG)
                .toFile(new File(PATH_TO_OUTPUT + "/mealy-to-more/moore.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mooreEdges;
    }

    void printMealyToMooreTable(Integer inputsCount, List<MooreEdge> mooreEdges) throws IOException {
        File output = new File(PATH_TO_OUTPUT + "/output.txt");
        var sortedMooreEdges = mooreEdges.stream().sorted((left, right) -> {
            int a = Integer.parseInt(left.x.substring(1));
            int b = Integer.parseInt(right.x.substring(1));
            return Integer.compare(a, b);
        }).collect(Collectors.toList());
        try (FileWriter writer = new FileWriter(output)) {
            int index = 0;
            for (MooreEdge mooreEdge : sortedMooreEdges) {
                writer.append(mooreEdge.to.q);
                if ((index + 1) % (mooreEdges.size() / inputsCount) == 0) {
                    writer.append("\n");
                } else {
                    writer.append(" ");
                }
                ++index;
            }
        }
    }

    private void fillMealyNodes(Integer nodesCount, List<MealyNode> mealyNodes) {
        for (Integer i = 0; i < nodesCount; i++) {
            MealyNode mealyNode = new MealyNode();

            mealyNode.q = "s" + i;

            mealyNodes.add(mealyNode);
        }
    }

    private void fillMealyEdges(Scanner scanner,
                                Integer inputsCount,
                                Integer nodesCount,
                                List<MealyNode> mealyNodes,
                                List<MealyEdge> mealyEdges) {
        var delimiter = scanner.delimiter();
        for (Integer i = 0; i < inputsCount; i++) {
            for (Integer j = 0; j < nodesCount; j++) {
                String q = scanner.useDelimiter("y").next().trim();
                String y = scanner.useDelimiter(delimiter).next().trim();

                MealyEdge mealyEdge = new MealyEdge();

                mealyEdge.x = "x" + i;
                mealyEdge.y = y;

                mealyEdge.to = findMealyNode(mealyNodes, q);
                mealyEdge.from = mealyNodes.get(j);

                mealyEdges.add(mealyEdge);
            }
        }
    }


    private MealyNode findMealyNode(List<MealyNode> mealyNodes, String q) {
        Optional<MealyNode> to = mealyNodes
            .stream()
            .filter(mealyNode -> mealyNode.q.equals(q))
            .findFirst();
        if (to.isEmpty()) {
            throw new RuntimeException("Node " + q + " not found");
        }
        return to.get();
    }

    private List<MooreEdge> mealyToMoore(List<MealyEdge> mealyEdges) {
        class MooreState {
            private String y;
            private String q;

            private MooreState(String y, String q) {
                this.y = y;
                this.q = q;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof MooreState)) return false;
                MooreState that = (MooreState) o;
                return Objects.equals(y, that.y) &&
                    Objects.equals(q, that.q);
            }

            @Override
            public int hashCode() {
                return Objects.hash(y, q);
            }
        }

        List<MooreEdge> mooreEdges = new ArrayList<>();

        HashSet<MealyEdge> uniqueMealyEdges = new HashSet<>(mealyEdges);

        HashMap<MooreState, String> stateToZ = new HashMap<>();
        HashMap<String, MooreState> zToState = new HashMap<>();

        List<MooreNode> mooreNodes = new ArrayList<>();

        var sortedUniqueMealyEdges = uniqueMealyEdges.stream().sorted((left, right) -> {
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

        int index = 0;
        for (MealyEdge uniqueMealyEdge : sortedUniqueMealyEdges) {
            MealyNode mealyFrom = uniqueMealyEdge.to;
            Optional<MealyEdge> mealyEdgeFrom = sortedUniqueMealyEdges
                .stream()
                .filter(mealyEdge -> mealyEdge.to.equals(mealyFrom))
                .findFirst();
            if (mealyEdgeFrom.isPresent()) {
                MooreState state = new MooreState(uniqueMealyEdge.y, mealyFrom.q);
                if (!stateToZ.containsKey(state)) {
                    stateToZ.put(state, "z" + index);
                    zToState.put("z" + index, state);
                    index++;
                }

                MooreNode mooreNode = new MooreNode();
                mooreNode.q = stateToZ.get(state);
                mooreNode.y = uniqueMealyEdge.y;

                mooreNodes.add(mooreNode);
            }
        }

        index = 0;
        for (MooreNode mooreFrom : mooreNodes) {
            MooreState state = zToState.get(mooreFrom.q);

            String qFrom = state.q;
            List<MealyEdge> mealyEdgeTo = mealyEdges
                .stream()
                .filter(mealyEdge -> mealyEdge.from.q.equals(qFrom))
                .collect(Collectors.toList());

            for (MealyEdge mealyEdge : mealyEdgeTo) {
                MooreEdge mooreEdge = new MooreEdge();

                MooreState mooreState = new MooreState(mealyEdge.y, mealyEdge.to.q);

                String mooreToZ = stateToZ.get(mooreState);

                Optional<MooreNode> mooreTo = mooreNodes.stream()
                    .filter(mooreNode -> mooreNode.q.equals(mooreToZ))
                    .findFirst();

                if (mooreTo.isPresent()) {
                    mooreEdge.from = mooreFrom;
                    mooreEdge.to = mooreTo.get();
                    mooreEdge.x = "x" + index++;

                    mooreEdges.add(mooreEdge);
                }
            }
            index = 0;
        }

        return mooreEdges;
    }
}
