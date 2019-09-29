package mealy_moore_converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        File input = new File("moore_input_example.txt");
        try (FileInputStream inputStream = new FileInputStream(input)) {
            Scanner scanner = new Scanner(inputStream);

            Integer inputsCount = scanner.nextInt();
            scanner.nextInt();
            Integer nodesCount = scanner.nextInt();

            String machineType = scanner.next().trim();

            if (machineType.equals("moore")) {
                MooreToMealyConverter converter = new MooreToMealyConverter();
                List<MooreEdge> mooreEdges = converter.parseMoore(scanner, inputsCount, nodesCount);
                converter.printMooreToMealyGraph(mooreEdges);
                converter.printMooreToMealyTable(inputsCount, mooreEdges);
            } else if (machineType.equals("mealy")) {
                MealyToMooreConverter converter = new MealyToMooreConverter();
                List<MealyEdge> mealyEdges = converter.parseMealy(scanner, inputsCount, nodesCount);
                converter.printMealyToMooreGraph(mealyEdges);
                converter.printMealyToMooreTable(inputsCount, mealyEdges);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
