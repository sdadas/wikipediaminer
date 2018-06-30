package org.wikipedia.miner.ms.model;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sławomir Dadas
 */
public class SpanPosition implements Comparable<SpanPosition> {

    private final int start;

    private final int end;

    public SpanPosition(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public int compareTo(@NotNull SpanPosition o) {
        return new CompareToBuilder()
                .append(this.start, o.start)
                .append(this.end, o.end)
                .toComparison();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpanPosition that = (SpanPosition) o;
        return new EqualsBuilder()
                .append(start, that.start)
                .append(end, that.end)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(start)
                .append(end)
                .toHashCode();
    }
}
