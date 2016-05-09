package com.cts.datapipeline;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private JobLaunch jobLaunch;
	public void run(String... arg0) throws Exception {
		JobParametersBuilder params = new JobParametersBuilder();
		params.addString("jobName", "First Job");
		params.addString("inputDirLocation", "csv/inputs");
		params.addString("outputDirLocation", "csv/outputs/domain.all.csv");
		
		jobLaunch.executeJob(params.toJobParameters());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
