package com.cts.datapipeline;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


public class PassThroughLineMapper implements LineMapper<String>{
	  private int lineCount;
	  
	  private StepExecution stepExecution;
	  
	   public PassThroughLineMapper(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	  @Override
	  public String mapLine(String line, int lineNumber) throws Exception {
		int count = -1;
		//System.out.println("Step Ex-->" + stepExecution);
		if(stepExecution.getExecutionContext().containsKey("line.count")) {
	    		 count = (int) stepExecution.getExecutionContext().get("line.count");
	        	System.out.println("TT Count process--->" + count);
	    }
		if(lineNumber > count-1) {
			System.out.println("Remove footer");
		}
	    return line.replace(",", "#");
	  }
	public int getLineCount() {
		return lineCount;
	}
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	public StepExecution getStepExecution() {
		return stepExecution;
	}
	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	
	
}
