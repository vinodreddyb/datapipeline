package com.cts.datapipeline.job.listeners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.util.FileSystemUtils;

public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	
   private String jobDataPath;
	
	public JobCompletionNotificationListener(String jobDataPath) {
		this.jobDataPath = jobDataPath;
	}
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		 Path path = Paths.get(jobDataPath + "/" + jobExecution.getJobId());
	        //if directory exists?
	        if (!Files.exists(path)) {
	            try {
	                Files.createDirectories(path);
	            } catch (IOException e) {
	                //fail to create directory
	                e.printStackTrace();
	            }
	        }
	        
	       jobExecution.getExecutionContext().put("jobDataPath", path.toString());

	}
 
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("Job Completed--->" + jobExecution.toString() + jobExecution.getJobId());
			 Path path = Paths.get(jobDataPath + "/" + jobExecution.getJobId());
			 boolean res= FileSystemUtils.deleteRecursively(path.toFile());
			 System.out.println("Deleted job data at--->" + path.toString() + " " + res);
			
		}
	}
}