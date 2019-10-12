package mealy_moore_converter;

import java.util.Objects;

class MooreEdge {
    MooreNode from;
    MooreNode to;
    String x;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MooreEdge)) return false;
        MooreEdge mooreEdge = (MooreEdge) o;
        return Objects.equals(from, mooreEdge.from) &&
            Objects.equals(to, mooreEdge.to) &&
            Objects.equals(x, mooreEdge.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, x);
    }
}
