package lib.models;

import java.util.Objects;

public class MealyNode {
    public String q;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealyNode)) return false;
        MealyNode mealyNode = (MealyNode) o;
        return Objects.equals(q, mealyNode.q);
    }

    @Override
    public int hashCode() {
        return Objects.hash(q);
    }
}
