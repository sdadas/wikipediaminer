package org.wikipedia.miner.examples;

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
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.wikipedia.miner.util.config.WikiPaths;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class SnippetAnnotator {


    private DocumentPreprocessor _preprocessor;

    private Disambiguator _disambiguator;

    private TopicDetector _topicDetector;

    private LinkDetector _linkDetector;

    private DocumentTagger _tagger;

    public SnippetAnnotator(Wikipedia wikipedia) throws Exception {
        _preprocessor = new WikiPreprocessor(wikipedia);
        _disambiguator = new Disambiguator(wikipedia) ;
        _topicDetector = new TopicDetector(wikipedia, _disambiguator);
        _linkDetector = new LinkDetector(wikipedia);
        _tagger = new WikiTagger();
    }

    public void annotate(String originalMarkup) throws Exception {
        PreprocessedDocument doc = _preprocessor.preprocess(originalMarkup);
        Collection<Topic> allTopics = _topicDetector.getTopics(doc, null);
        List<Topic> bestTopics = _linkDetector.getBestTopics(allTopics, 0.5);
        System.out.println("\nTopics that are probably good links:");
        for (Topic topic : bestTopics) {
            String weight = String.format("[%.2f]", topic.getWeight());
            System.out.println(" - " + topic.getTitle() + " " + weight) ;
        }

        String newMarkup = _tagger.tag(doc, bestTopics, DocumentTagger.RepeatMode.ALL) ;
        System.out.println("\nAugmented markup:\n" + newMarkup + "\n") ;
    }

    public static void main(String args[]) throws Exception {

        WikipediaConfiguration conf = new WikipediaConfiguration(WikiPaths.findConfig("plwiki")) ;
        Wikipedia wikipedia = new Wikipedia(conf, false) ;

        SnippetAnnotator annotator = new SnippetAnnotator(wikipedia) ;
        annotator.annotate(
        "Wino – napój alkoholowy uzyskiwany w wyniku fermentacji moszczu winogronowego. Istnieje wiele rodzajów win, " +
                "co związane jest z mnogością odmian winorośli, oddziaływaniem środowiska na ich wzrost i różnymi " +
                "technikami winifikacji. Można wyróżnić wina białe, różowe oraz czerwone. Ze względu na zawartość " +
                "cukru można je podzielić na wytrawne, półwytrawne, półsłodkie i słodkie. Mogą to być wina musujące " +
                "lub niemusujące. Wino składa się z wody (75%–90%), etanolu, glicerolu, polisacharydów oraz różnych " +
                "kwasów i związków fenolowych. Poza tym w winie znajdują się sole mineralne oraz witaminy. Łącznie " +
                "zawiera ponad tysiąc substancji, nie wszystkie są dobrze poznane. Nie zawsze wyraz wino odnosi się " +
                "do fermentowanego napoju pozyskanego z winogron, owoców winorośli właściwej. Istnieje wiele " +
                "rozmaitych win wyprodukowanych z innych owoców, także tropikalnych. Nauką zajmującą się produkcją " +
                "wina jest enologia."
        );
    }
}
