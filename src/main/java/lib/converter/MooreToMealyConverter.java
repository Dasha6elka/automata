package lib.converter;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import lib.graph.LinkSources;
import lib.models.MealyEdge;
import lib.models.MealyNode;
import lib.models.MooreEdge;
import lib.models.MooreNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.graph;

public class MooreToMealyConverter {
    private String PATH_TO_OUTPUT = "output";

    private LinkSources linkSources = new LinkSources();

    public List<MooreEdge> parseMoore(Scanner scanner, Integer inputsCount, Integer nodesCount) {
        ArrayList<MooreNode> mooreNodes = new ArrayList<>();
        ArrayList<MooreEdge> mooreEdges = new ArrayList<>();

        fillMooreNodes(scanner, nodesCount, mooreNodes);
        fillMooreEdges(scanner, inputsCount, nodesCount, mooreNodes, mooreEdges);

        return mooreEdges;
    }

    public void printMooreToMealyGraph(List<MooreEdge> mooreEdges) {
        List<LinkSource> mooreSources = linkSources.createMooreLinkSources(mooreEdges);

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

        List<LinkSource> mealySources = mooreToMealy(mooreEdges, linkSources);

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

    public void printMooreToMealyTable(Integer nodesCount, List<MooreEdge> mooreEdges) throws IOException {
        File output = new File(PATH_TO_OUTPUT + "/output.txt");
        try (FileWriter writer = new FileWriter(output)) {
            int index = 0;
            for (MooreEdge mooreEdge : mooreEdges) {
                writer.append(mooreEdge.to.q).append(mooreEdge.to.y);
                if ((index + 1) % nodesCount == 0) {
                    writer.append("\n");
                } else {
                    writer.append(" ");
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

                mooreEdge.x = "x" + i;

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

    private List<LinkSource> mooreToMealy(List<MooreEdge> mooreEdges, LinkSources linkSources) {
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

        return linkSources.createMealyLinkSources(mealyEdges);
    }
}