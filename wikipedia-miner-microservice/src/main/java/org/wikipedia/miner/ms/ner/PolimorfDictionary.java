package org.wikipedia.miner.ms.ner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public class PolimorfDictionary {

    private static final Logger LOG = LoggerFactory.getLogger(PolimorfDictionary.class);

    private File polimorfPath;

    private Set<String> commonWords;

    private Set<String> firstNames;

    private Set<String> lastNames;

    public PolimorfDictionary(File polimorfPath) throws IOException {
        this.polimorfPath = polimorfPath;
        this.commonWords = new HashSet<String>();
        this.firstNames = new HashSet<String>();
        this.lastNames = new HashSet<String>();
        loadPolimorf();
    }

    private void loadPolimorf() throws IOException {
        if(!polimorfPath.exists()) {
            LOG.error("Polimorf dictionary not found on path {}, cancelling.", this.polimorfPath.getCanonicalPath());
            return;
        }
        LineIterator iter = FileUtils.lineIterator(this.polimorfPath, "UTF-8");
        while(iter.hasNext()) {
            String line = iter.next();
            String[] values = StringUtils.split(line, "\t");
            Validate.isTrue(values.length == 4 || values.length == 3, "wrong line: " + line);
            String word = values[0];
            String label = values.length == 4 ? values[3] : "pospolita";
            String lowerWord = StringUtils.lowerCase(word);
            if(label.equals("pospolita")) {
                this.commonWords.add(lowerWord);
            } else if(label.equals("imię")) {
                this.firstNames.add(lowerWord);
            } else if(label.equals("nazwisko")) {
                this.lastNames.add(lowerWord);
            }
        }
    }


    public boolean isCommonWord(String word) {
        return commonWords.contains(word.toLowerCase());
    }

    public boolean isFirstName(String word) {
        return firstNames.contains(word.toLowerCase());
    }

    public boolean isLastName(String word) {
        return lastNames.contains(word.toLowerCase());
    }
}
