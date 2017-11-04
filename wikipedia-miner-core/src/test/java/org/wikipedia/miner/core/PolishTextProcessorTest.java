package org.wikipedia.miner.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wikipedia.miner.util.text.polish.PolishTextProcessor;

/**
 * Tests for PolishTextProcessor.
 *
 * @see org.wikipedia.miner.util.text.polish.PolishTextProcessor
 * @author Sławomir Dadas
 */
public class PolishTextProcessorTest {

    private PolishTextProcessor processor;

    @Before
    public void init() {
        this.processor = new PolishTextProcessor();
    }

    @Test
    public void processTextTest() {
        assertText(null, "");
        assertText("", "");
        assertText(" \n\r", "");
        assertText("Ala ma kota", "ala mieć kot");
        assertText("Ala jest córką aktora Ala Pacino", "ala być córka aktor al pacino");
        assertText("Zażółcić gęślą jaźń.", "zażółcić gęśl jaźń");
        assertText("Pchnąć jeża w tę łódź i osiem skrzyń fig.", "pchnąć jeż w ten łódź i osiem skrzynia figa");
    }

    private void assertText(String input, String expected) {
        String result = processor.processText(input);
        Assert.assertEquals(result.trim(), expected.trim());
    }
}
