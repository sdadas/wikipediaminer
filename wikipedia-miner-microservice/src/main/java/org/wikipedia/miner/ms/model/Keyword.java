package org.wikipedia.miner.ms.model;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class Keyword implements Serializable {

    private String name;

    private Double weight;

    public Keyword() {
    }

    public Keyword(String name, Double weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
