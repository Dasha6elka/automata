package mealy_moore_converter;

import java.util.Objects;

class MealyEdge {
    MealyNode from;
    MealyNode to;
    String x;
    String y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealyEdge)) return false;
        MealyEdge mealyEdge = (MealyEdge) o;
        return Objects.equals(to, mealyEdge.to) &&
            Objects.equals(y, mealyEdge.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, y);
    }
}
