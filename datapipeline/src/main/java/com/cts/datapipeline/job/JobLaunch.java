package com.cts.datapipeline.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.cts.datapipeline.input.JobStep;
import com.cts.datapipeline.input.StepItems;
import com.cts.datapipeline.input.constants.StepClasses;
import com.cts.datapipeline.job.chunks.ReplacerAndSkipHeaderFooterLineItemProcessor;
import com.cts.datapipeline.job.listeners.JobCompletionNotificationListener;
import com.cts.datapipeline.job.listeners.OutputFileListener;

@Component
public class JobLaunch {
	@Autowired
	JobLauncher jobLauncher;
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public ApplicationContext context;
	
	@Value("${job.data.basepath}")
	String jobDataBasePath;

	
	public void executeJob(JobParameters params, List<JobStep> steps) throws Exception {

		List<Step> orderSteps = createSteps(steps);

		SimpleJob job = new SimpleJob();
		job.setName("SimpleJob");
		job.setJobParametersIncrementer(new RunIdIncrementer());
		job.setSteps(orderSteps);
		job.setJobRepository((JobRepository) context.getBean("jobRepository"));
        job.registerJobExecutionListener(new JobCompletionNotificationListener(jobDataBasePath));
		jobLauncher.run(job, params);

	}

	/*
	 * Job myjob = jobBuilderFactory.get("Replacer Job1").incrementer(new
	 * RunIdIncrementer()).listener(joblistener()) .flow(null).end().build();
	 */
	/*
	 * jobBuilder.get("job1") .start(step1)
	 * .next(step2).on("CONTINUE").to(step2).on("FINISHED").end() .build();
	 */
	// http://forum.spring.io/forum/spring-projects/batch/125257-creating-spring-batch-flow-job-dynamically
	
	
	@SuppressWarnings("unchecked")
	private List<Step> createSteps(List<JobStep> steps) {
		List<Step> orderSteps = new ArrayList<>();
		for (JobStep step : steps) {
			String stepId = step.getStepId();
			if (stepId.equals("replacer")) {
				StepItems items = StepClasses.replacer.getItems();
				MultiResourcePartitioner mergeFilePartitioner = (MultiResourcePartitioner) context
						.getBean(items.getPartitioner());
				FlatFileItemReader<String> itemReader = (FlatFileItemReader<String>) context.getBean(items.getReader());
				FlatFileItemWriter<String> itemWriter = (FlatFileItemWriter<String>) context.getBean(items.getWriter());
				ReplacerAndSkipHeaderFooterLineItemProcessor processor = (ReplacerAndSkipHeaderFooterLineItemProcessor) context.getBean(items.getProcessor());
				OutputFileListener listener = (OutputFileListener) context.getBean(items.getListener());

				Step replacerStep = stepBuilderFactory.get("replacerStep").<String, String> chunk(1).reader(itemReader).processor(processor)
						.writer(itemWriter).listener(listener).build();
				Step replacerPartitionStep = stepBuilderFactory.get("PartitionStep").partitioner(replacerStep)
						.partitioner("replacerStep", mergeFilePartitioner).taskExecutor(new SyncTaskExecutor()).build();
				orderSteps.add(replacerPartitionStep);

			} else if (stepId.equals("merger")) {
				StepItems items = StepClasses.merger.getItems();
				MultiResourceItemReader<String> itemReader = (MultiResourceItemReader<String>) context
						.getBean(items.getReader());
				FlatFileItemWriter<String> itemWriter = (FlatFileItemWriter<String>) context.getBean(items.getWriter());

				Step mergeStep = stepBuilderFactory.get("replacerStep").<String, String> chunk(1).reader(itemReader)
						.writer(itemWriter).build();
				orderSteps.add(mergeStep);
			}
		}
		return orderSteps;
	}

	

	/*
	 * public GetCurrentResourceChunkListener listener1() throws Exception {
	 * 
	 * GetCurrentResourceChunkListener listener = new
	 * GetCurrentResourceChunkListener();
	 * listener.setProxy(multResourceItemReaderReplace); return listener; }
	 */
}
