package org.wikipedia.miner.ms;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Sławomir Dadas
 */
@SpringBootApplication
public class Application implements ApplicationRunner {

    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!args.containsOption("wiki")) {
            throw new IllegalArgumentException("Please provide wiki option i.e. --wiki=enwiki");
        }
    }
}
