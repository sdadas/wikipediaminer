package org.wikipedia.miner.ms.model;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class TextDoc implements Serializable {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
