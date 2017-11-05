package org.wikipedia.miner.ms.service;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;
import org.wikipedia.miner.annotation.Disambiguator;
import org.wikipedia.miner.annotation.Topic;
import org.wikipedia.miner.annotation.TopicDetector;
import org.wikipedia.miner.annotation.preprocessing.DocumentPreprocessor;
import org.wikipedia.miner.annotation.preprocessing.PreprocessedDocument;
import org.wikipedia.miner.annotation.preprocessing.WikiPreprocessor;
import org.wikipedia.miner.annotation.tagging.DocumentTagger;
import org.wikipedia.miner.annotation.tagging.WikiTagger;
import org.wikipedia.miner.annotation.weighting.LinkDetector;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.ms.model.KeywordDoc;
import org.wikipedia.miner.ms.model.TextDoc;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.wikipedia.miner.util.config.WikiPaths;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
@Service
public class WikiService {

    private String wiki;

    private Wikipedia wikipedia;

    private DocumentPreprocessor preprocessor;

    private Disambiguator disambiguator;

    private TopicDetector topicDetector;

    private LinkDetector linkDetector;

    private DocumentTagger tagger;

    @Autowired
    public WikiService(ApplicationArguments args) throws Exception {
        this.wiki = args.getOptionValues("wiki").get(0);
        WikipediaConfiguration conf = new WikipediaConfiguration(WikiPaths.findConfig(this.wiki)) ;
        this.wikipedia = new Wikipedia(conf, false);
        this.initTools();
    }

    private void initTools() throws Exception {
        this.preprocessor = new WikiPreprocessor(this.wikipedia);
        this.disambiguator = new Disambiguator(this.wikipedia);
        this.topicDetector = new TopicDetector(this.wikipedia, this.disambiguator);
        this.linkDetector = new LinkDetector(this.wikipedia);
        this.tagger = new WikiTagger();
    }

    public KeywordDoc getTopics(TextDoc input) throws Exception {
        PreprocessedDocument doc = this.preprocessor.preprocess(input.getText());
        Collection<Topic> topics = topicDetector.getTopics(doc, null);
        List<Topic> best = linkDetector.getBestTopics(topics, 0.5);

        KeywordDoc result = new KeywordDoc();
        for (Topic topic : best) {
            result.addKeyword(topic.getTitle(), topic.getWeight());
        }
        result.setText(this.tagger.tag(doc, best, DocumentTagger.RepeatMode.ALL));
        return result;
    }

    @PreDestroy
    private void cleanup() {
        IOUtils.closeQuietly(wikipedia);
    }
}
