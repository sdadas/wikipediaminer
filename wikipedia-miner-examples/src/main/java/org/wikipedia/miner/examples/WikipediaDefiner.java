package org.wikipedia.miner.examples;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.wikipedia.miner.util.config.WikiPaths;

public class WikipediaDefiner {

    public static void main(String args[]) throws Exception {

        WikipediaConfiguration conf = new WikipediaConfiguration(WikiPaths.findConfig("plwiki"));
        conf.clearDatabasesToCache();

        Wikipedia wikipedia = new Wikipedia(conf, false);
        Article article = wikipedia.getArticleByTitle("Wikipedia");
        System.out.println(article.getSentenceMarkup(0));
        wikipedia.close();
    }
}
