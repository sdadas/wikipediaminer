package org.wikipedia.miner.extract.steps.pageDepth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.mapred.AvroJob;
import org.apache.avro.mapred.Pair;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.log4j.Logger;
import org.wikipedia.miner.extract.DumpExtractor;
import org.wikipedia.miner.extract.model.struct.PageDepthSummary;
import org.wikipedia.miner.extract.model.struct.PageDetail;
import org.wikipedia.miner.extract.steps.IterativeStep;
import org.wikipedia.miner.extract.steps.pageDepth.DepthCombinerOrReducer.Counts;
import org.wikipedia.miner.extract.steps.pageDepth.DepthCombinerOrReducer.DepthCombiner;
import org.wikipedia.miner.extract.steps.pageDepth.DepthCombinerOrReducer.DepthReducer;
import org.wikipedia.miner.extract.steps.sortedPages.PageSortingStep;
import org.wikipedia.miner.extract.util.UncompletedStepException;


public class PageDepthStep extends IterativeStep {

	private static Logger logger = Logger.getLogger(PageDepthStep.class) ;
	
	private PageSortingStep finalPageSummaryStep ;

	private Map<Counts,Long> counts ;



	public PageDepthStep(Path workingDir, int iteration, PageSortingStep finalPageSummaryStep) throws IOException {
		super(workingDir, iteration);

		this.finalPageSummaryStep = finalPageSummaryStep ;
	}

	@Override
	public int run(String[] args) throws Exception {
		
		logger.info("Starting page depth step (iteration " + getIteration() + ")");
		
		if (isFinished()) {
			logger.info(" - already completed");
			loadCounts() ;
			return 0 ;
		} else {
			reset() ;
		}
		
		JobConf conf = new JobConf(PageDepthStep.class);
		DumpExtractor.configureJob(conf, args) ;

		conf.setJobName("WM: page depth (" + getIteration() + ")");
		
		if (getIteration() == 0) {
		
			FileInputFormat.setInputPaths(conf, getWorkingDir() + Path.SEPARATOR + finalPageSummaryStep.getDirName());
			AvroJob.setInputSchema(conf, Pair.getPairSchema(Schema.create(Type.INT),PageDetail.getClassSchema()));
			
			DistributedCache.addCacheFile(new Path(conf.get(DumpExtractor.KEY_LANG_FILE)).toUri(), conf);
			
			AvroJob.setMapperClass(conf, InitialDepthMapper.class);
			
		} else {
			
			FileInputFormat.setInputPaths(conf, getWorkingDir() + Path.SEPARATOR + getDirName(getIteration()-1));
			AvroJob.setInputSchema(conf, Pair.getPairSchema(Schema.create(Type.INT),PageDepthSummary.getClassSchema()));
			
			AvroJob.setMapperClass(conf, SubsequentDepthMapper.class);
		}
			
		AvroJob.setOutputSchema(conf, Pair.getPairSchema(Schema.create(Type.INT),PageDepthSummary.getClassSchema()));
				
		AvroJob.setCombinerClass(conf, DepthCombiner.class) ;
		AvroJob.setReducerClass(conf, DepthReducer.class);
		
		FileOutputFormat.setOutputPath(conf, getDir());
		
		RunningJob runningJob = JobClient.runJob(conf);
	
		if (runningJob.getJobState() == JobStatus.SUCCEEDED) {	
			finish(runningJob) ;
			return 0 ;
		}
		
		throw new UncompletedStepException() ;
	}

	public boolean furtherIterationsRequired() {
		return counts.get(Counts.unforwarded) > 0 ;
	}


	@Override
	public String getDirName(int iteration) {
		return "pageDepth_" + iteration ;
	}

	private Path getCountsPath() {
		return new Path(getDir() + Path.SEPARATOR + "counts") ;
	}

	
	
	private void saveCounts() throws IOException {
		FSDataOutputStream out = getHdfs().create(getCountsPath());
		
		for (Counts c:Counts.values()) {
			
			out.writeUTF(c.name()) ;
			
			Long count = counts.get(c) ;
			if (count != null)
				out.writeLong(count) ;
			else
				out.writeLong(0L) ;
		}
		
		out.close();
	}
	
	private void loadCounts() throws IOException {
		
		counts = new HashMap<Counts,Long>() ;
		
		
		FSDataInputStream in = getHdfs().open(getCountsPath());
		
		while (in.available() > 0) {
			
			String c = in.readUTF() ;
			
			Long count = in.readLong() ;
			
			counts.put(Counts.valueOf(c), count) ;
		}
	
		in.close() ;
		
	}



	public void finish(RunningJob runningJob) throws IOException {
		super.finish(runningJob) ;
		counts = new HashMap<Counts,Long>() ;
		for (Counts count:Counts.values()) {
			long value = runningJob.getCounters().getCounter(count);
			counts.put(count, value) ;
		}
		saveCounts() ;
	}
}
