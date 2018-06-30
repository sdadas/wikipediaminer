package org.wikipedia.miner.annotation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.wikipedia.miner.model.Wikipedia;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sławomir Dadas
 */
public class ArticleClassMapper {

    private Wikipedia wikipedia;

    private Map<Integer, String> mapping;

    public ArticleClassMapper(Wikipedia wikipedia) throws IOException {
        this.wikipedia = wikipedia;
        this.mapping = loadMapping();
    }

    private Map<Integer, String> loadMapping() throws IOException {
        File directory = wikipedia.getConfig().getDataDirectory();
        File file = new File(directory, "mapping.csv");
        if(!file.exists()) {
            throw new IOException(file.getCanonicalPath() + " does not exist!");
        }

        Map<Integer, String> res = new HashMap<Integer, String>();
        LineIterator iter = FileUtils.lineIterator(file, "UTF-8");
        while(iter.hasNext()) {
            String line = iter.next();
            parseMappingLine(res, line);
        }
        return res;
    }

    private void parseMappingLine(Map<Integer, String> res, String line) {
        if(StringUtils.isBlank(line)) return;
        String[] split = StringUtils.split(line, ',');
        Validate.isTrue(split.length == 3, "invalid line in file: " + line);
        Integer id = Integer.parseInt(split[0]);
        String className = StringUtils.strip(split[2]);
        res.put(id, className);
    }

    public String topicClass(Topic topic) {
        int id = topic.getId();
        return mapping.get(id);
    }
}
