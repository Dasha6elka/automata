package minimization;

import lib.converter.MealyToMooreConverter;
import lib.graph.GraphPrinter;
import lib.graph.LinkSources;
import lib.minimization.MealyMinimization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Minimization {
    private static final String PATH_TO_OUTPUT = "output/minimization";

    private static GraphPrinter graphPrinter = new GraphPrinter();
    private static LinkSources linkSources = new LinkSources();

    public static void main(String[] args) {
        var input = new File("input.txt");
        try (var stream = new FileInputStream(input)) {
            var scanner = new Scanner(stream);

            var inputsCount = scanner.nextInt();
            scanner.nextInt();
            var nodesCount = scanner.nextInt();

            var machineType = scanner.next().trim();

            if (machineType.equals("moore")) {
                // TODO: do code
            } else if (machineType.equals("mealy")) {
                var mealyMinimization = new MealyMinimization();
                var converter = new MealyToMooreConverter();
                var mealyEdges = converter.parseMealy(scanner, inputsCount, nodesCount);
                var minimizedMealyEdges = mealyMinimization.minimizeGraph(mealyEdges, inputsCount);
                var mealyLinkSources = linkSources.createMealyLinkSources(minimizedMealyEdges);
                graphPrinter.printGraph("Minimized Mealy Graph", mealyLinkSources, PATH_TO_OUTPUT + "/mealy.png");
                graphPrinter.printMealyTable(inputsCount, minimizedMealyEdges, PATH_TO_OUTPUT + "/output.txt");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
