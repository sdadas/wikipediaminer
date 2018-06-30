package org.wikipedia.miner.util.text.polish;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;
import org.apache.commons.lang3.StringUtils;
import org.wikipedia.miner.util.text.TextProcessor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Sławomir Dadas
 */
public class MorfologikTextProcessor extends TextProcessor {

    private PolishStemmer stemmer;

    private Map<String, String> overwrite;

    public MorfologikTextProcessor() {
        this.stemmer = new PolishStemmer();
        this.overwrite = createOverWriteMap();
    }

    private Map<String, String> createOverWriteMap() {
        return ImmutableMap.<String, String>builder()
                .put("ale", "ale")
                .build();
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
                } else if(overwrite.containsKey(lowerWord)) {
                    results.add(overwrite.get(lowerWord));
                } else {
                    List<WordData> data = stemmer.lookup(word);
                    String stem = data.isEmpty() ? lowerWord : data.get(0).getStem().toString();
                    results.add(stem.toLowerCase());
                }
            }
        }
        return Joiner.on(' ').join(results);
    }
}
