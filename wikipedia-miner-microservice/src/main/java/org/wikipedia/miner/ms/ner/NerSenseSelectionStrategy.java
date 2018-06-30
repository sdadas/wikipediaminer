package org.wikipedia.miner.ms.ner;

import org.apache.commons.lang3.StringUtils;
import org.wikipedia.miner.annotation.SenseSelectionStrategy;
import org.wikipedia.miner.annotation.TopicDetector;
import org.wikipedia.miner.annotation.TopicReference;
import org.wikipedia.miner.util.Position;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class NerSenseSelectionStrategy implements SenseSelectionStrategy {

    private final PolimorfDictionary dict;

    private final String text;

    public NerSenseSelectionStrategy(PolimorfDictionary dictionary, String text) {
        this.dict = dictionary;
        this.text = text;
    }

    @Override
    public boolean accept(TopicReference ref, TopicDetector.CachedSense sense) {
        Position pos = ref.getPosition();
        String fragment = text.substring(pos.getStart(), pos.getEnd());
        if(!fragment.contains(" ") && StringUtils.isAllLowerCase(fragment) && dict.isCommonWord(fragment)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<TopicDetector.CachedSense> transform(TopicReference ref, List<TopicDetector.CachedSense> senses) {
        int limit = Math.min(senses.size(), 3);
        return senses.subList(0, limit);
    }
}
