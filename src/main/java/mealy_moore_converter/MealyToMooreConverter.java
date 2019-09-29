package mealy_moore_converter;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

class MealyToMooreConverter {
    private String PATH_TO_OUTPUT = "output";

    List<MealyEdge> parseMealy(Scanner scanner, Integer inputsCount, Integer nodesCount) {
        ArrayList<MealyNode> mealyNodes = new ArrayList<>();
        ArrayList<MealyEdge> mealyEdges = new ArrayList<>();

        fillMealyNodes(nodesCount, mealyNodes);
        fillMealyEdges(scanner, inputsCount, nodesCount, mealyNodes, mealyEdges);

        return mealyEdges;
    }

    void printMealyToMooreGraph(List<MealyEdge> mealyEdges) {
        List<LinkSource> mealySources = createMealyLinkSources(mealyEdges);

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

        List<LinkSource> mooreSources = mealyToMoore(mealyEdges);

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
    }

    void printMealyToMooreTable(Integer inputsCount, List<MealyEdge> mealyEdges) throws IOException {
        File output = new File(PATH_TO_OUTPUT + "/output.txt");
        try (FileWriter writer = new FileWriter(output)) {
            Integer index = 0;
            for (MealyEdge mealyEdge : mealyEdges) {
                writer.append(mealyEdge.to.q).append(" ");
                if (index.equals(inputsCount - 1)) {
                    writer.append("\n");
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

                mealyEdge.x = "x" + (i + 1);
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

    private List<LinkSource> createMealyLinkSources(List<MealyEdge> mealyEdges) {
        List<LinkSource> sources = new ArrayList<>();
        for (MealyEdge mealyEdge : mealyEdges) {
            Label label = Label.of(mealyEdge.x + "/" + mealyEdge.y);
            sources.add(node(mealyEdge.from.q).link(to(node(mealyEdge.to.q)).with(label)));
        }
        return sources;
    }

    private List<LinkSource> mealyToMoore(List<MealyEdge> mealyEdges) {
        List<MooreEdge> mooreEdges = new ArrayList<>();

        int index = 0;
        for (MealyEdge mealyEdge : mealyEdges) {
            MooreNode mooreFrom = new MooreNode();

            mooreFrom.q = mealyEdge.from.q;

            String y = mealyEdges.get(index).y;
            if (y == null || y.isEmpty()) {
                mooreFrom.y = "-";
            } else {
                mooreFrom.y = y;
            }

            MooreNode mooreTo = new MooreNode();
            mooreTo.q = mealyEdge.to.q;
            mooreTo.y = mealyEdge.y;

            MooreEdge mooreEdge = new MooreEdge();
            mooreEdge.from = mooreFrom;
            mooreEdge.to = mooreTo;
            mooreEdge.x = mealyEdge.x;

            mooreEdges.add(mooreEdge);

            ++index;
        }

        return createMooreLinkSources(mooreEdges);
    }

    private List<LinkSource> createMooreLinkSources(List<MooreEdge> mooreEdges) {
        List<LinkSource> sources = new ArrayList<>();
        for (MooreEdge mooreEdge : mooreEdges) {
            Label label = Label.of(mooreEdge.x);
            Node from = node(mooreEdge.from.q).with("xlabel", mooreEdge.from.y);
            Node to = node(mooreEdge.to.q).with("xlabel", mooreEdge.to.y);
            sources.add(from.link(to(to).with(label)));
        }
        return sources;
    }

}
