package org.wikipedia.miner.ms.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public class LabelledSpan {

    private final SpanPosition position;

    private final List<SpanSense> senses;

    private final String fragment;

    private Set<String> labels = new HashSet<String>();

    public LabelledSpan(SpanPosition position, String fragment) {
        this.position = position;
        this.fragment = fragment;
        this.senses = new ArrayList<SpanSense>();
    }

    public SpanPosition getPosition() {
        return position;
    }

    public List<SpanSense> getSenses() {
        return senses;
    }

    public void addSense(SpanSense sense, boolean skipDuplicateLabels) {
        if(skipDuplicateLabels && this.labels.contains(sense.getLabel())) return;
        this.senses.add(sense);
        this.labels.add(sense.getLabel());
    }

    public String getFragment() {
        return fragment;
    }

    public Set<String> getLabels() {
        return labels;
    }
}
