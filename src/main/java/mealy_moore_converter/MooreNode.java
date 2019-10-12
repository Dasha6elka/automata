package mealy_moore_converter;

import java.util.Objects;

class MooreNode {
    String q;
    String y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MooreNode)) return false;
        MooreNode mooreNode = (MooreNode) o;
        return Objects.equals(q, mooreNode.q) &&
            Objects.equals(y, mooreNode.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, y);
    }
}
