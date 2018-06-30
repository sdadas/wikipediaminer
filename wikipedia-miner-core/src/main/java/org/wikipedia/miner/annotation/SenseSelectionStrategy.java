package org.wikipedia.miner.annotation;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public interface SenseSelectionStrategy {

    boolean accept(TopicReference ref, TopicDetector.CachedSense sense);

    List<TopicDetector.CachedSense> transform(TopicReference ref, List<TopicDetector.CachedSense> senses);
}
