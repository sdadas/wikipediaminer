package org.wikipedia.miner.ms.ner;

import javafx.scene.effect.SepiaTone;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public class PolimorfDictionary {

    private File polimorfPath;

    private Set<String> commonWords;

    public PolimorfDictionary(File polimorfPath) throws IOException {
        this.polimorfPath = polimorfPath;
        this.commonWords = loadPolimorf();
    }

    private Set<String> loadPolimorf() throws IOException {
        Set<String> res = new HashSet<String>();
        LineIterator iter = FileUtils.lineIterator(this.polimorfPath, "UTF-8");
        while(iter.hasNext()) {
            String line = iter.next();
            String[] values = StringUtils.split(line, "\t");
            Validate.isTrue(values.length == 4 || values.length == 3, "wrong line: " + line);
            if(values.length == 4 && values[3].equals("pospolita")) {
                res.add(StringUtils.lowerCase(values[0]));
            }
        }
        return res;
    }


    public boolean isCommonWord(String word) {
        return commonWords.contains(word.toLowerCase());
    }
}
