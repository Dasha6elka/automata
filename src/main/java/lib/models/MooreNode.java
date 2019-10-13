package lib.models;

import java.util.Objects;

public class MooreNode {
    public String q;
    public String y;

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
