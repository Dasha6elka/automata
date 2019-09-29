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

class MooreToMealyConverter {
    String PATH_TO_OUTPUT = "output";

    List<MooreEdge> parseMoore(Scanner scanner, Integer inputsCount, Integer nodesCount) {
        ArrayList<MooreNode> mooreNodes = new ArrayList<>();
        ArrayList<MooreEdge> mooreEdges = new ArrayList<>();

        fillMooreNodes(scanner, nodesCount, mooreNodes);
        fillMooreEdges(scanner, inputsCount, nodesCount, mooreNodes, mooreEdges);

        return mooreEdges;
    }

    void printMooreToMealyGraph(List<MooreEdge> mooreEdges) {
        List<LinkSource> mooreSources = createMooreLinkSources(mooreEdges);

        Graph mooreGraph = graph("Moore Graph")
            .directed()
            .with(mooreSources);

        try {
            Graphviz
                .fromGraph(mooreGraph)
                .width(1024)
                .render(Format.PNG)
                .toFile(new File(PATH_TO_OUTPUT + "/moore-to-mealy/moore.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<LinkSource> mealySources = mooreToMealy(mooreEdges);

        Graph mealyGraph = graph("Mealy Graph")
            .directed()
            .with(mealySources);

        try {
            Graphviz
                .fromGraph(mealyGraph)
                .width(1024)
                .render(Format.PNG)
                .toFile(new File(PATH_TO_OUTPUT + "/moore-to-mealy/mealy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void printMooreToMealyTable(Integer inputsCount, List<MooreEdge> mooreEdges) throws IOException {
        File output = new File(PATH_TO_OUTPUT + "/output.txt");
        try (FileWriter writer = new FileWriter(output)) {
            Integer index = 0;
            for (MooreEdge mooreEdge : mooreEdges) {
                writer.append(mooreEdge.to.q).append(mooreEdge.to.y).append(" ");
                if (index.equals(inputsCount)) {
                    writer.append("\n");
                }
                ++index;
            }
        }
    }

    private void fillMooreNodes(Scanner scanner, Integer nodesCount, List<MooreNode> mooreNodes) {
        for (Integer i = 0; i < nodesCount; i++) {
            MooreNode mooreNode = new MooreNode();

            String y = scanner.next();

            mooreNode.q = "q" + i;
            mooreNode.y = y;

            mooreNodes.add(mooreNode);
        }
    }


    private void fillMooreEdges(Scanner scanner,
                                Integer inputsCount,
                                Integer nodesCount,
                                List<MooreNode> mooreNodes,
                                List<MooreEdge> mooreEdges) {
        for (Integer i = 0; i < inputsCount; i++) {
            for (Integer j = 0; j < nodesCount; j++) {
                MooreEdge mooreEdge = new MooreEdge();

                mooreEdge.x = "x" + (i + 1);

                String q = scanner.next().trim();

                mooreEdge.to = findMooreNode(mooreNodes, q);
                mooreEdge.from = mooreNodes.get(j);

                mooreEdges.add(mooreEdge);
            }
        }
    }

    private MooreNode findMooreNode(List<MooreNode> mooreNodes, String q) {
        Optional<MooreNode> to = mooreNodes
            .stream()
            .filter(mooreNode -> mooreNode.q.contains(q))
            .findFirst();

        if (to.isEmpty()) {
            throw new RuntimeException("Node " + " to not found");
        }
        return to.get();
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

    private List<LinkSource> mooreToMealy(List<MooreEdge> mooreEdges) {
        List<MealyEdge> mealyEdges = new ArrayList<>();

        for (MooreEdge mooreEdge : mooreEdges) {
            MealyNode mealyFrom = new MealyNode();
            mealyFrom.q = mooreEdge.from.q;

            MealyNode mealyTo = new MealyNode();
            mealyTo.q = mooreEdge.to.q;

            MealyEdge mealyEdge = new MealyEdge();
            mealyEdge.from = mealyFrom;
            mealyEdge.to = mealyTo;
            mealyEdge.x = mooreEdge.x;
            mealyEdge.y = mooreEdge.to.y;

            mealyEdges.add(mealyEdge);
        }

        return createMealyLinkSources(mealyEdges);
    }


    private List<LinkSource> createMealyLinkSources(List<MealyEdge> mealyEdges) {
        List<LinkSource> sources = new ArrayList<>();
        for (MealyEdge mealyEdge : mealyEdges) {
            Label label = Label.of(mealyEdge.x + "/" + mealyEdge.y);
            sources.add(node(mealyEdge.from.q).link(to(node(mealyEdge.to.q)).with(label)));
        }
        return sources;
    }
}
