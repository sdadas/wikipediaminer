package org.wikipedia.miner.extract;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.wikipedia.miner.extract.labelSenses.LabelSensesStep;
import org.wikipedia.miner.extract.pageDepth.PageDepthStep;
import org.wikipedia.miner.extract.pageSummary.PageSummaryStep;
import org.wikipedia.miner.extract.util.LanguageConfiguration;


/**
 * @author dnk2
 * <p>
 * This class extracts summaries (link graphs, etc) from Wikipedia xml dumps.
 * It calls a sequence of Hadoop Map/Reduce jobs to do so in a scalable, timely fashion.
 */
public class DumpExtractor2 extends Configured implements Tool {

    private Configuration conf;

    private String[] args;

    private Path inputFile;
    private Path langFile;
    private String lang;
    private Path sentenceModel;
    private Path workingDir;
    private Path finalDir;

    private LanguageConfiguration lc;
    //private Logger logger ;


    public static final String KEY_INPUT_FILE = "wm.inputDir";
    public static final String KEY_OUTPUT_DIR = "wm.workingDir";
    public static final String KEY_LANG_FILE = "wm.langFile";
    public static final String KEY_LANG_CODE = "wm.langCode";
    public static final String KEY_SENTENCE_MODEL = "wm.sentenceModel";

    public static final String LOG_ORPHANED_PAGES = "orphanedPages";
    public static final String LOG_WEIRD_LABEL_COUNT = "wierdLabelCounts";
    public static final String LOG_MEMORY_USE = "memoryUsage";


    public static final String OUTPUT_SITEINFO = "final/siteInfo.xml";
    public static final String OUTPUT_PROGRESS = "tempProgress.csv";
    public static final String OUTPUT_TEMPSTATS = "tempStats.csv";
    public static final String OUTPUT_STATS = "final/stats.csv";

    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new DumpExtractor2(), args);
        System.exit(res);
    }

    public static JobConf configureJob(JobConf conf, String[] args) {

        conf.set(KEY_INPUT_FILE, args[0]);
        conf.set(KEY_LANG_FILE, args[1]);
        conf.set(KEY_LANG_CODE, args[2]);
        conf.set(KEY_SENTENCE_MODEL, args[3]);
        conf.set(KEY_OUTPUT_DIR, args[4]);

        //set a reasonable number of maps. This is going to be ignored for very large inputs (e.g. the en wiki dump) anyway.
        conf.setNumMapTasks(16);

        //force one reducer by default. These don't take very long, and multiple reducers would make finalise file functions more complicated.
        conf.setNumReduceTasks(1);

        //many of our tasks require pre-loading lots of data, may as well reuse this as much as we can.
        //conf.setNumTasksToExecutePerJvm(-1) ;

        //conf.setInt("mapred.tasktracker.map.tasks.maximum", 2) ;
        //conf.setInt("mapred.tasktracker.reduce.tasks.maximum", 1) ;
        //conf.set("mapred.child.java.opts", "-Xmx500M -Dapple.awt.UIElement=true");

        //conf.setBoolean("mapred.used.genericoptionsparser", true) ;
        conf.set("mapreduce.map.java.opts", "-Xmx3000m");
        conf.set("mapreduce.map.memory.mb", "4096");
        conf.setInt("mapreduce.job.running.map.limit", 16);
        conf.set("mapreduce.reduce.java.opts", "-Xmx3000m");
        conf.set("mapreduce.reduce.memory.mb", "4096");

        return conf;
    }


    private FileSystem getFileSystem(Path path) throws IOException {
        return path.getFileSystem(conf);
    }


    private Path getPath(String pathStr) {
        return new Path(pathStr);
    }


    private FileStatus getFileStatus(Path path) throws IOException {
        FileSystem fs = path.getFileSystem(conf);
        return fs.getFileStatus(path);
    }


    private void configure() throws Exception {

        if (args.length != 6) {
            throw new IllegalArgumentException("Please specify a xml dump of wikipedia, " +
                    "a language.xml config file, a language code, an openNLP sentence detection model, " +
                    "an hdfs writable working directory, and an output directory");
        }

        //check input file
        inputFile = getPath(args[0]);
        FileStatus fs = getFileStatus(inputFile);
        if (fs.isDir() || !fs.getPermission().getUserAction().implies(FsAction.READ)) {
            throw new IOException("'" + inputFile + " is not readable or does not exist");
        }

        //check lang file and language
        langFile = getPath(args[1]);
        lang = args[2];
        lc = new LanguageConfiguration(langFile.getFileSystem(conf), lang, langFile);
        if (lc == null)
            throw new IOException("Could not load language configuration for '" + lang + "' from '" + langFile + "'");

        sentenceModel = new Path(args[3]);
        fs = getFileStatus(sentenceModel);
        if (fs.isDir() || !fs.getPermission().getUserAction().implies(FsAction.READ))
            throw new IOException("'" + sentenceModel + " is not readable or does not exist");

        //check working directory
        workingDir = new Path(args[4]);

        if (!getFileSystem(workingDir).exists(workingDir))
            getFileSystem(workingDir).mkdirs(workingDir);

        fs = getFileStatus(workingDir);
        if (!fs.isDir() || !fs.getPermission().getUserAction().implies(FsAction.WRITE))
            throw new IOException("'" + workingDir + " is not a writable directory");

        //set up directory where final data will be placed
        finalDir = new Path(args[5]);

        if (getFileSystem(finalDir).exists(finalDir))
            getFileSystem(finalDir).delete(finalDir, true);

        getFileSystem(finalDir).mkdirs(finalDir);

        fs = getFileStatus(finalDir);
        if (!fs.isDir() || !fs.getPermission().getUserAction().implies(FsAction.WRITE))
            throw new IOException("'" + workingDir + " is not a writable directory");

    }

    private int run() throws Exception {

        Logger.getLogger(DumpExtractor2.class).info("Extracting site info");
        extractSiteInfo();

        //extract basic page summaries
        int summaryIteration = 0;
        PageSummaryStep summaryStep;
        while (true) {
            summaryStep = new PageSummaryStep(workingDir, summaryIteration);
            ToolRunner.run(this.conf, summaryStep, args);
            if (!summaryStep.furtherIterationsRequired()) {
                break;
            } else {
                summaryIteration++;
            }
        }

        //calculate page depths
        int depthIteration = 0;
        while (true) {
            PageDepthStep depthStep = new PageDepthStep(workingDir, depthIteration, summaryStep);
            ToolRunner.run(this.conf, depthStep, args);
            if (!depthStep.furtherIterationsRequired()) {
                break;
            } else {
                depthIteration++;
            }
        }

        //gather senses
        LabelSensesStep sensesStep = new LabelSensesStep(workingDir, summaryStep);
        ToolRunner.run(this.conf, sensesStep, args);
        return 0;
    }


    private void extractSiteInfo() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(getFileSystem(inputFile).open(inputFile)));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(getFileSystem(workingDir).create(new Path(workingDir + "/" + OUTPUT_SITEINFO))));

        String line = null;
        boolean startedWriting = false;

        while ((line = reader.readLine()) != null) {

            if (!startedWriting && line.matches("\\s*\\<siteinfo\\>\\s*"))
                startedWriting = true;

            if (startedWriting) {
                writer.write(line);
                writer.newLine();

                if (line.matches("\\s*\\<\\/siteinfo\\>\\s*"))
                    break;
            }
        }

        reader.close();
        writer.close();
    }


    @Override
    public int run(String[] args) throws Exception {
        this.conf = this.getConf();
        this.args = args;
        configure();
        return run();
    }
}
