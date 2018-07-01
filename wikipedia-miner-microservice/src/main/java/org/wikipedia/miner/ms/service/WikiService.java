package org.wikipedia.miner.ms.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikipedia.miner.annotation.*;
import org.wikipedia.miner.annotation.preprocessing.DocumentPreprocessor;
import org.wikipedia.miner.annotation.preprocessing.PreprocessedDocument;
import org.wikipedia.miner.annotation.preprocessing.WikiPreprocessor;
import org.wikipedia.miner.annotation.tagging.DocumentTagger;
import org.wikipedia.miner.annotation.tagging.WikiTagger;
import org.wikipedia.miner.annotation.weighting.LinkDetector;
import org.wikipedia.miner.db.WEnvironment;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.ms.model.*;
import org.wikipedia.miner.ms.ner.NerSenseSelectionStrategy;
import org.wikipedia.miner.ms.ner.PolimorfDictionary;
import org.wikipedia.miner.util.Position;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.wikipedia.miner.util.config.WikiPaths;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    private ArticleClassMapper mapper;

    private PolimorfDictionary polimorf;

    @Autowired
    public WikiService() throws Exception {
        this.wiki = ObjectUtils.firstNonNull(System.getenv("WIKI_PATH"), System.getProperty("wiki.path"));
        if(StringUtils.isBlank(this.wiki)) {
            throw new IllegalArgumentException("Please provide wiki option i.e. -Dwiki.path=enwiki");
        }
        WikipediaConfiguration conf = new WikipediaConfiguration(WikiPaths.findConfig(this.wiki));
        buildEnvironment(conf);
        this.wikipedia = new Wikipedia(conf, false);
        this.initTools();
    }

    private void buildEnvironment(WikipediaConfiguration conf) throws Exception {
        if (conf.getDatabaseDirectory().exists()) {
            return;
        }
        if (conf.getDataDirectory() == null || !conf.getDataDirectory().isDirectory()) {
            throw new IOException("Data directory not valid at " + conf.getDatabaseDirectory().getAbsolutePath());
        }
        System.setProperty("entityExpansionLimit", "2147480000");
        System.setProperty("totalEntitySizeLimit", "2147480000");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "2147480000");
        WEnvironment.buildEnvironment(conf, conf.getDataDirectory(), false) ;
    }

    private void initTools() throws Exception {
        this.mapper = new ArticleClassMapper(this.wikipedia);
        this.preprocessor = new WikiPreprocessor(this.wikipedia);
        this.disambiguator = new Disambiguator(this.wikipedia);
        this.topicDetector = new TopicDetector(this.wikipedia, this.disambiguator);
        this.topicDetector.allowDisambiguations(true);
        this.topicDetector.setDisambiguationPolicy(TopicDetector.DisambiguationPolicy.LOOSE);
        this.linkDetector = new LinkDetector(this.wikipedia);
        this.tagger = new WikiTagger();
        File polimorfPath = new File(this.wikipedia.getConfig().getDataDirectory(), "PoliMorf-0.6.7.tab");
        this.polimorf = new PolimorfDictionary(polimorfPath);
    }

    public KeywordDoc getTopics(TextDoc input) throws Exception {
        PreprocessedDocument doc = this.preprocessor.preprocess(input.getText());
        Collection<Topic> topics = topicDetector.getTopics(doc.getPreprocessedText(), null);
        List<Topic> best = linkDetector.getBestTopics(topics, 0.5);

        KeywordDoc result = new KeywordDoc();
        for (Topic topic : best) {
            result.addKeyword(topic.getTitle(), topic.getWeight());
        }
        result.setText(this.tagger.tag(doc, best, DocumentTagger.RepeatMode.ALL));
        return result;
    }

    public List<LabelledSpan> getNamedEntities(TextDoc input, boolean skipOther, boolean skipDuplicateLabels)
            throws Exception {
        SenseSelectionStrategy selectionStrategy = new NerSenseSelectionStrategy(mapper, polimorf, input.getText());
        Collection<Topic> topics = topicDetector.getTopics(input.getText(), null, selectionStrategy);
        Map<SpanPosition, LabelledSpan> res = new TreeMap<SpanPosition, LabelledSpan>();
        for (Topic topic: topics) {
            spansFromTopic(topic, res, input.getText(), skipOther, skipDuplicateLabels);
        }
        return new ArrayList<LabelledSpan>(res.values());
    }

    private void spansFromTopic(Topic topic, Map<SpanPosition, LabelledSpan> res, String text,
                                boolean skipOther, boolean skipDuplicateLabels) throws Exception {

        String clazz = ObjectUtils.firstNonNull(this.mapper.topicClass(topic), "other");
        if(skipOther && clazz.equals("other")) return;
        for (Position position: topic.getPositions()) {
            SpanPosition pos = new SpanPosition(position.getStart(), position.getEnd());
            String fragment = text.substring(pos.getStart(), pos.getEnd());
            LabelledSpan span = res.get(pos);
            if(span == null) {
                span = new LabelledSpan(pos, fragment);
                res.put(pos, span);
            }
            if(span.getLabels().contains(clazz) && skipDuplicateLabels) continue;
            double proba = topic.getAverageDisambigConfidence();
            double relateness = topic.getRelatednessToOtherTopics();
            SpanSense sense = new SpanSense(topic.getTitle(), topic.getId(), clazz, proba, relateness);
            span.addSense(sense, skipDuplicateLabels);
        }
    }

    @PreDestroy
    private void cleanup() {
        IOUtils.closeQuietly(wikipedia);
    }
}
