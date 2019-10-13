package minimization;

import lib.models.MealyEdge;
import lib.converter.MealyToMooreConverter;
import lib.models.MooreEdge;
import lib.converter.MooreToMealyConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Minimization {
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
            } else if (machineType.equals("mealy")) {
                MealyToMooreConverter converter = new MealyToMooreConverter();
                List<MealyEdge> mealyEdges = converter.parseMealy(scanner, inputsCount, nodesCount);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
