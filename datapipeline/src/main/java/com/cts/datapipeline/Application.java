package com.cts.datapipeline;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cts.datapipeline.input.JobStep;
import com.cts.datapipeline.job.JobLaunch;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private JobLaunch jobLaunch;
	public void run(String... arg0) throws Exception {
		JobParametersBuilder params = new JobParametersBuilder();
		params.addString("jobName", "First Job");
		params.addString("inputDirLocation", "csv/inputs");
		params.addString("outputDirLocation", "file:csv/outputs/");
		params.addLong("time",System.currentTimeMillis());
		
		
		params.addString("mergeInputDirLocation", "csv/outputs");
		params.addString("mergeOutputDirLocation", "csv/merge/merge.csv");
		
		JobStep step1 = new JobStep();
		step1.setStepId("replacer");
		
		JobStep step2 = new JobStep();
		step2.setStepId("merger");
		
		List<JobStep> steps = new ArrayList<>();
		steps.add(step1);
		steps.add(step2);
		jobLaunch.executeJob(params.toJobParameters(),steps);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
