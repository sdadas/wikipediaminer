package org.wikipedia.miner.util.text.polish;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;
import org.apache.commons.lang3.StringUtils;
import org.wikipedia.miner.util.text.TextProcessor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Sławomir Dadas
 */
public class MorfologikTextProcessor extends TextProcessor {

    private PolishStemmer stemmer;

    private Map<String, String> cache;

    public MorfologikTextProcessor() {
        this.stemmer = new PolishStemmer();
        this.cache = createCache();
    }

    private Map<String, String> createCache() {
        Map<String, String> res = new HashMap<String, String>();
        res.put("się", "się");
        res.put("ale", "ale");
        res.put("mam", "mieć");
        res.put("mamy", "mieć");
        return res;
    }

    @Override
    public String processText(String text) {
        if(StringUtils.isBlank(text)) return StringUtils.EMPTY;
        List<String> results = new LinkedList<String>();
        String[] words = StringUtils.split(text);
        for (String word : words) {
            if(StringUtils.isNotBlank(word)) {
                String lowerWord = StringUtils.lowerCase(word);
                if(word.length() <= 2) {
                    results.add(lowerWord);
                } else if(cache.containsKey(lowerWord)) {
                    results.add(cache.get(lowerWord));
                } else {
                    List<WordData> data = stemmer.lookup(word);
                    String stem = data.isEmpty() ? lowerWord : data.get(0).getStem().toString();
                    cache.put(lowerWord, stem.toLowerCase());
                    results.add(stem.toLowerCase());
                }
            }
        }
        return Joiner.on(' ').join(results);
    }
}
