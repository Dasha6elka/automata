package minimization;

import guru.nidi.graphviz.model.LinkSource;
import lib.converter.MealyToMooreConverter;
import lib.converter.MooreToMealyConverter;
import lib.graph.GraphPrinter;
import lib.graph.LinkSources;
import lib.minimization.MealyMinimization;
import lib.minimization.MooreMinimization;
import lib.models.MealyEdge;
import lib.models.MooreEdge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Minimization {
    private static final String PATH_TO_OUTPUT = "output/minimization";

    private static GraphPrinter graphPrinter = new GraphPrinter();
    private static LinkSources linkSources = new LinkSources();

    public static void main(String[] args) {
        File input = new File("input.txt");
        try (FileInputStream inputStream = new FileInputStream(input)) {
            Scanner scanner = new Scanner(inputStream);

            Integer inputsCount = scanner.nextInt();
            scanner.nextInt();
            Integer nodesCount = scanner.nextInt();

            String machineType = scanner.next().trim();

            if (machineType.equals("moore")) {
                MooreMinimization mooreMinimization = new MooreMinimization();
                MooreToMealyConverter converter = new MooreToMealyConverter();
                List<MooreEdge> mooreEdges = converter.parseMoore(scanner, inputsCount, nodesCount);
                List<MooreEdge> minimizedMooreEdges = mooreMinimization.minimizeGraph(mooreEdges);
                List<LinkSource> mooreLinkSources = linkSources.createMooreLinkSources(minimizedMooreEdges);
                graphPrinter.printGraph("Minimized Moore Graph", mooreLinkSources, PATH_TO_OUTPUT + "/moore.png");
                graphPrinter.printMooreTable(inputsCount, minimizedMooreEdges, PATH_TO_OUTPUT + "/output.txt");
            } else if (machineType.equals("mealy")) {
                MealyMinimization mealyMinimization = new MealyMinimization();
                MealyToMooreConverter converter = new MealyToMooreConverter();
                List<MealyEdge> mealyEdges = converter.parseMealy(scanner, inputsCount, nodesCount);
                List<MealyEdge> minimizedMealyEdges = mealyMinimization.minimizeGraph(mealyEdges);
                List<LinkSource> mealyLinkSources = linkSources.createMealyLinkSources(minimizedMealyEdges);
                graphPrinter.printGraph("Minimized Mealy Graph", mealyLinkSources, PATH_TO_OUTPUT + "/mealy.png");
                graphPrinter.printMealyTable(inputsCount, minimizedMealyEdges, PATH_TO_OUTPUT + "/output.txt");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
