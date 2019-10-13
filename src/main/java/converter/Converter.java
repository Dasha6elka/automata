package converter;

import guru.nidi.graphviz.model.LinkSource;
import lib.converter.MealyToMooreConverter;
import lib.converter.MooreToMealyConverter;
import lib.graph.GraphPrinter;
import lib.graph.LinkSources;
import lib.models.MealyEdge;
import lib.models.MooreEdge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Converter {
    private static final String PATH_TO_OUTPUT = "output/converter";

    private static LinkSources linkSources = new LinkSources();
    private static GraphPrinter graphPrinter = new GraphPrinter();

    public static void main(String[] args) {
        File input = new File("input.txt");
        try (FileInputStream inputStream = new FileInputStream(input)) {
            Scanner scanner = new Scanner(inputStream);

            Integer inputsCount = scanner.nextInt();
            scanner.nextInt();
            Integer nodesCount = scanner.nextInt();

            String machineType = scanner.next().trim();

            if (machineType.equals("moore")) {
                MooreToMealyConverter converter = new MooreToMealyConverter();
                List<MooreEdge> mooreEdges = converter.parseMoore(scanner, inputsCount, nodesCount);
                List<LinkSource> mooreSources = linkSources.createMooreLinkSources(mooreEdges);
                graphPrinter.printGraph("Moore Graph", mooreSources, PATH_TO_OUTPUT + "/moore-to-mealy/moore.png");
                List<MealyEdge> mealyEdges = converter.mooreToMealy(mooreEdges);
                List<LinkSource> mealyLinkSources = linkSources.createMealyLinkSources(mealyEdges);
                graphPrinter.printGraph("Mealy Graph", mealyLinkSources, PATH_TO_OUTPUT + "/moore-to-mealy/mealy.png");
                graphPrinter.printMealyTable(nodesCount, mealyEdges, PATH_TO_OUTPUT + "/output.txt");
            } else if (machineType.equals("mealy")) {
                MealyToMooreConverter converter = new MealyToMooreConverter();
                List<MealyEdge> mealyEdges = converter.parseMealy(scanner, inputsCount, nodesCount);
                List<LinkSource> mealySources = linkSources.createMealyLinkSources(mealyEdges);
                graphPrinter.printGraph("Mealy Graph", mealySources, PATH_TO_OUTPUT + "/mealy-to-more/mealy.png");
                List<MooreEdge> mooreEdges = converter.mealyToMoore(mealyEdges);
                List<LinkSource> mooreLinkSources = linkSources.createMooreLinkSources(mooreEdges);
                graphPrinter.printGraph("Moore Graph", mooreLinkSources, PATH_TO_OUTPUT + "/mealy-to-more/moore.png");
                graphPrinter.printMooreTable(inputsCount, mooreEdges, PATH_TO_OUTPUT + "/output.txt");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
