package com.cts.datapipeline;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobLaunch {
	@Autowired
	JobLauncher jobLauncher;
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	MultiResourceItemReader<String> multResourceItemReaderReplace;

	@Autowired
	FlatFileItemWriter<String> mergerWriter;

	@Autowired
	public DataSource dataSource;

	public void executeJob(JobParameters params) throws Exception {
		Job myjob = jobBuilderFactory.get("Replacer Job1").incrementer(new RunIdIncrementer()).listener(joblistener())
				.flow(step1()).end().build();
		
		jobLauncher.run(myjob, params);

		/*
		 * jobBuilder.get("job1") .start(step1)
		 * .next(step2).on("CONTINUE").to(step2).on("FINISHED").end() .build();
		 */
		// http://forum.spring.io/forum/spring-projects/batch/125257-creating-spring-batch-flow-job-dynamically

	}

	public JobExecutionListener joblistener() {
		return new JobCompletionNotificationListener();
	}

	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1").<String, String> chunk(1).reader(multResourceItemReaderReplace)
				// .processor(new SkipFooterLineItemProcessor())
				.writer(mergerWriter).listener(listener1()).build();
	}

	public GetCurrentResourceChunkListener listener1() throws Exception {

		GetCurrentResourceChunkListener listener = new GetCurrentResourceChunkListener();
		listener.setProxy(multResourceItemReaderReplace);
		return listener;
	}
}
