package com.cts.datapipeline.merge;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.file.FlatFileFooterCallback;

public class FooterWriter extends StepExecutionListenerSupport implements FlatFileFooterCallback {
private StepExecution stepExecution;
	
	@Override
	public void writeFooter(Writer writer) throws IOException {
		writer.write("footer - here ");
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}


}
