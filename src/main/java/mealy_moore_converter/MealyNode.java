package mealy_moore_converter;

import java.util.Objects;

class MealyNode {
    String q;

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
