package org.wikipedia.miner.ms.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wikipedia.miner.ms.model.KeywordDoc;
import org.wikipedia.miner.ms.model.LabelledSpan;
import org.wikipedia.miner.ms.model.TextDoc;
import org.wikipedia.miner.ms.service.WikiService;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
@RestController
public class WikiController {

    private WikiService service;

    @Autowired
    public WikiController(WikiService service) {
        this.service = service;
    }

    @RequestMapping("/doc/topics")
    public KeywordDoc topics(@RequestBody TextDoc input) throws Exception {
        return service.getTopics(input);
    }

    @RequestMapping("/doc/ner")
    public List<LabelledSpan> ner(@RequestBody TextDoc input,
                                  @RequestParam(required = false, defaultValue = "true") Boolean skipOther,
                                  @RequestParam(required = false, defaultValue = "true") Boolean skipDuplicateLabels)
            throws Exception {
        return service.getNamedEntities(input, skipOther, skipDuplicateLabels);
    }
}
