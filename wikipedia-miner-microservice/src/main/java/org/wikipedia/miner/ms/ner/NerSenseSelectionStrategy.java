package org.wikipedia.miner.ms.ner;

import org.apache.commons.lang3.StringUtils;
import org.wikipedia.miner.annotation.SenseSelectionStrategy;
import org.wikipedia.miner.annotation.TopicDetector;
import org.wikipedia.miner.annotation.TopicReference;
import org.wikipedia.miner.util.Position;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public class NerSenseSelectionStrategy implements SenseSelectionStrategy {

    private final PolimorfDictionary dict;

    private final String text;

    private final Set<Integer> startOfSentence;

    public NerSenseSelectionStrategy(PolimorfDictionary dictionary, String text) {
        this.dict = dictionary;
        this.text = text;
        this.startOfSentence = startOfSequencePositions();
    }

    private Set<Integer> startOfSequencePositions() {
        Set<Integer> res = new HashSet<Integer>();
        res.add(0);
        int pos = text.indexOf(". ");
        while(pos >= 0) {
            res.add(pos + 3);
            pos = text.indexOf(". ", pos + 1);
        }
        return res;
    }

    @Override
    public boolean accept(TopicReference ref, TopicDetector.CachedSense sense) {
        Position pos = ref.getPosition();
        String fragment = text.substring(pos.getStart(), pos.getEnd());
        if(StringUtils.isNumericSpace(fragment)) {
            return false;
        }
        if(startOfSentence.contains(pos.getStart())) {
            char[] chars = fragment.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            fragment = new String(chars);
        }
        if(StringUtils.isAllLowerCase(fragment)) {
            String[] words = StringUtils.split(fragment);
            for (String word: words) {
                if(!dict.isCommonWord(word)) return true;
            }
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
