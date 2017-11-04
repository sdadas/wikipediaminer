package org.wikipedia.miner.util.text.polish;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Polish;
import org.wikipedia.miner.util.text.Cleaner;
import org.wikipedia.miner.util.text.TextProcessor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Text processor implementation for Polish language.
 * It uses LanguageTool for morphological analysis and lemmatization.
 *
 * @author Sławomir Dadas
 */
public class PolishTextProcessor extends TextProcessor {

    private final JLanguageTool languageTool;

    private final Cleaner cleaner;

    public PolishTextProcessor() {
        this.languageTool = new JLanguageTool(new Polish());
        this.cleaner = new Cleaner();
    }

    @Override
    public String processText(String text) {
        if(StringUtils.isBlank(text)) return StringUtils.EMPTY;
        List<String> words = new LinkedList<String>();
        try {
            List<AnalyzedSentence> sentences = languageTool.analyzeText(text);
            for (AnalyzedSentence sentence : sentences) {
                for (AnalyzedTokenReadings token : sentence.getTokens()) {
                    words.add(processWord(token));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        String result = Joiner.on(' ').skipNulls().join(words);
        return this.cleaner.processText(result);
    }

    private String processWord(AnalyzedTokenReadings token) {
        if(token.isWhitespace() || token.isLinebreak()) return " ";
        if(isIgnored(token)) return null;
        for (AnalyzedToken reading : token.getReadings()) {
            if(!reading.hasNoTag() && StringUtils.isNotBlank(reading.getLemma())) {
                return reading.getLemma();
            }
        }
        return token.getToken();
    }

    private boolean isIgnored(AnalyzedTokenReadings token) {
        return token.isSentenceStart() || StringUtils.equals(token.getToken(), "\uFEFF");
    }
}
