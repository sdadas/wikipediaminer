Wikipedia Miner
==============

This is a fork of [Neuw84](https://github.com/Neuw84/wikipediaminer) version of Wikipedia Miner, which is itself a fork of 
[original project by David Milne](https://github.com/dnmilne/wikipediaminer). This version adds support
for Polish language, fixes some minor bugs and contains changes required to run the code 
on modern JDKs and Hadoop Clusters (in particular Cloudera Hadoop 5.x).

Documentation at: https://github.com/dnmilne/wikipediaminer/wiki


### Building Wikipedia from scratch - full workflow

#### 1. Get official wiki dump

- Go to [https://dumps.wikimedia.org/backup-index.html](https://dumps.wikimedia.org/backup-index.html)
- Find Wikipedia you want to download, for example [https://dumps.wikimedia.org/enwiki/](https://dumps.wikimedia.org/enwiki/)
- Download the latest available **pages-articles** archive, named **{wiki_type}-{date}-pages-articles.xml.bz2**

#### 2. Build CSV data files

- You need a working Hadoop Cluster for this step. If you don't have an access to such cluster,
  you can run it locally as a single node cluster. Unfortunately the extraction process is quite
  time consuming, so running it on a single machine could take a day or a few.
- Upload the following files to your HDFS:
  - {1} Extracted wikipedia dump: **{wiki_type}-{date}-pages-articles.xml**
  - {2} **config/languages.xml** file. The file already has configurations for a few wikis, you need to
    [modify it](https://github.com/dnmilne/wikipediaminer/wiki/Language-dependent-configuration) to
    support others.
  - {3} An OpenNLP sentence detection model for your language. Models for popular languages 
      (English, German, French, Spanish etc.) can be found on the Internet. Additionally, this
      repository contains model for Polish, English and German. For other languages you'd probably 
      need to train your own model.*
- Run `mvn package`, this will build fat jar in **wikipedia-miner-extract/target**
- Submit the compiled jar as a job to your Hadoop Cluster and pass paths to uploaded files as arguments:
  `hadoop jar wikipedia-miner-extract-{version}-job.jar {1:wiki_dump} {2:langauges.xml} {language_code} {3:sentence_detector} {working_dir} {output_dir}`
  - `language_code` is a code that defines wiki configuration in **languages.xml** file
  - `working_dir` is a directory where the job will output its temporary results
  - `output_dir` is a directory where the job will output its final results
- Copy all the files from **output_dir** to your local machine

\* Alternatively, refactoring DumpExtractor class and decoupling it from OpenNLP classes would allow 
to use other sentence detection models (e.g. LanguageTool supports over 20 languages out of the box).

#### 3. Build Wikipedia Miner database

- In this step you will use downloaded wiki dump as well as the files created in previous step.
- Create your own **TextProcessor** implementation or select an existing one. Creating TextProcessor for
   new language is easy with **LanguageTool** library, see `org.wikipedia.miner.util.text.polish.PolishTextProcessor`
   as an example. Likely your language is already supported.
- Create stopwords file for your language or [get it from the Internet](https://github.com/6/stopwords-json/tree/master/src).
- Create wikipedia instance configuration, see **configs/wikipedia-template.xml**. 
- Run **EnvironmentBuilder** class, passing path to instance configuration as an argument. 
  This should take less than an hour for small wikis and up to a few hours for large ones.
- Ideally, all the files related to a single wikipedia instance should be located in the same directory:
  configuration, stopwords, csv files, xml dump, database dir.
- The database is ready to use. Set `WIKI_HOME` environment variable to point to a directory where wikipedia
  directories are stored (that it, the parent directory to the directory containing your wikipedia files).
- Run `org.wikipedia.miner.examples.WikipediaDefiner` in **wikipedia-miner-examples**
  to confirm everything is working correctly (change `findConfig()` method argument to point to your instance).