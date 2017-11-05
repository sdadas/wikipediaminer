package org.wikipedia.miner.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wikipedia.miner.ms.model.KeywordDoc;
import org.wikipedia.miner.ms.model.TextDoc;
import org.wikipedia.miner.ms.service.WikiService;

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
}
