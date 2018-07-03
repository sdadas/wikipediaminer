package org.wikipedia.miner.ms.ner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.wikipedia.miner.annotation.ArticleClassMapper;
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

    private final ArticleClassMapper classMapper;

    public NerSenseSelectionStrategy(ArticleClassMapper mapper, PolimorfDictionary dictionary, String text) {
        this.dict = dictionary;
        this.text = text;
        this.classMapper = mapper;
    }

    @Override
    public boolean accept(TopicReference ref, TopicDetector.CachedSense sense) {
        Position pos = ref.getPosition();
        String fragment = text.substring(pos.getStart(), pos.getEnd());
        // ignore all short and numeric tokens
        if(StringUtils.length(fragment) < 2 || StringUtils.isNumericSpace(fragment)) {
            return false;
        }
        // if single word that is a person name i labelled as geogName or orgName
        if(dict.isFirstName(fragment) && StringUtils.equals(WordUtils.capitalize(fragment), fragment)) {
            String label = classMapper.idClass(sense.getId());
            if(StringUtils.equals(label, "geogName") || StringUtils.equals(label, "orgName")) {
                return false;
            }
        }
        // if start of sentence, make first word lowercase
        if(pos.getStart() == 0 || (pos.getStart() >= 2 && text.charAt(pos.getStart() - 2) == '.')) {
            char[] chars = fragment.toCharArray();
            if(chars.length > 1 && Character.isUpperCase(chars[1])) {
                // if word has more than one uppercase letter, leave it unchanged
            } else {
                chars[0] = Character.toLowerCase(chars[0]);
            }
            fragment = new String(chars);
        }
        // ignore all lowercase common words
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
