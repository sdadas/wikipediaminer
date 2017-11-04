package org.wikipedia.miner.util.text;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.languagetool.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstract TextProcessor for analyzing text with LanguageTools.
 * One can easily implement new TextProcessor for new language by extending this class
 * and providing custom createLanguage() method.
 *
 * @author Sławomir Dadas
 */
public abstract class LanguageToolTextProcessor extends TextProcessor {

    private final JLanguageTool languageTool;

    private final Cleaner cleaner;

    protected abstract Language createLanguage();

    public LanguageToolTextProcessor() {
        this.languageTool = new JLanguageTool(this.createLanguage());
        this.cleaner = new Cleaner();
    }

    @Override
    public String processText(String text) {
        if(StringUtils.isBlank(text)) return StringUtils.EMPTY;
        List<String> words = new LinkedList<String>();
        try {
            List<AnalyzedSentence> sentences = languageTool.analyzeText(text);
            for (AnalyzedSentence sentence : sentences) {
                processSentence(sentence, words);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        String result = Joiner.on(' ').skipNulls().join(words);
        return this.cleaner.processText(result);
    }

    private void processSentence(AnalyzedSentence sentence, List<String> output) {
        for (AnalyzedTokenReadings token : sentence.getTokens()) {
            output.add(processWord(token));
        }
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
