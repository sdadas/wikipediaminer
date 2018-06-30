package org.wikipedia.miner.ms.model;

/**
 * @author Sławomir Dadas
 */
public class SpanSense {

    private String article;

    private int articleId;

    private String label;

    private double probability;

    private double relateness;

    public SpanSense(String article, int articleId, String label, double probability, double relateness) {
        this.article = article;
        this.articleId = articleId;
        this.label = label;
        this.probability = probability;
        this.relateness = relateness;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getRelateness() {
        return relateness;
    }

    public void setRelateness(double relateness) {
        this.relateness = relateness;
    }
}
