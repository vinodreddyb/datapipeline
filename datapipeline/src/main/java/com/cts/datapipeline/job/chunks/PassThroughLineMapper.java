package com.cts.datapipeline.job.chunks;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.LineMapper;

public class PassThroughLineMapper implements LineMapper<String> {
	private int lineCount;
	private StepExecution stepExecution;

	public PassThroughLineMapper(StepExecution stepExecution) {
	  this.stepExecution = stepExecution;
	}
	@Override
	public String mapLine(String line, int lineNumber) throws Exception {
		int count = -1;
		if (stepExecution.getExecutionContext().containsKey("cFile")) {
			String cFile = stepExecution.getExecutionContext().getString("cFile");
			count = (int) stepExecution.getExecutionContext().get(cFile);
			System.out.println(cFile + " " + count);
		}
		System.out.println(line + " ::: " + lineNumber);

		if (lineNumber > count - 1) {
			return null;
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
