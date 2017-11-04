package org.wikipedia.miner.util.text.polish;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.languagetool.*;
import org.languagetool.language.Polish;
import org.wikipedia.miner.util.text.Cleaner;
import org.wikipedia.miner.util.text.LanguageToolTextProcessor;
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
public class PolishTextProcessor extends LanguageToolTextProcessor {

    @Override
    protected Language createLanguage() {
        return new Polish();
    }
}
