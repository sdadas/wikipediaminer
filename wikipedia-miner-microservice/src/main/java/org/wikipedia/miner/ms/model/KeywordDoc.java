package org.wikipedia.miner.ms.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class KeywordDoc extends TextDoc {

    private List<Keyword> keywords = new ArrayList<Keyword>();

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
    }

    public void addKeyword(String keyword, Double weight) {
        this.keywords.add(new Keyword(keyword, weight));
    }
}
