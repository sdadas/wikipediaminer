package org.wikipedia.miner.util.text.polish;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.wikipedia.miner.util.text.TextProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class StempelTextProcessor extends TextProcessor {

    private StempelStemmer stemmer;

    public StempelTextProcessor() {
        String resource = "/org/apache/lucene/analysis/pl/stemmer_20000.tbl";
        InputStream is = StempelTextProcessor.class.getResourceAsStream(resource);
        try {
            this.stemmer = new StempelStemmer(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String processText(String text) {
        if(StringUtils.isBlank(text)) return StringUtils.EMPTY;
        List<String> results = new LinkedList<String>();
        String[] words = StringUtils.split(text);
        for (String word : words) {
            if(StringUtils.isNotBlank(word)) {
                StringBuilder sb = stemmer.stem(word);
                String stem = sb != null ? sb.toString() : word;
                results.add(stem.toLowerCase());
            }
        }
        return Joiner.on(' ').join(results);
    }
}
