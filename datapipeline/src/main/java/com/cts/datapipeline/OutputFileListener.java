package com.cts.datapipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;

public class OutputFileListener {
	private String outputKeyName = "outputFile";

	private String inputKeyName = "fileName";

	private String path = "file:./build/output/";
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setOutputKeyName(String outputKeyName) {
		this.outputKeyName = outputKeyName;
	}

	public void setInputKeyName(String inputKeyName) {
		this.inputKeyName = inputKeyName;
	}

	@BeforeStep
	public void createOutputNameFromInput(StepExecution stepExecution) throws FileNotFoundException, IOException {
		ExecutionContext executionContext = stepExecution.getExecutionContext();
		String inputName = stepExecution.getStepName().replace(":", "-");
		if (executionContext.containsKey(inputKeyName)) {
			inputName = executionContext.getString(inputKeyName);
		}
		if (!executionContext.containsKey(outputKeyName)) {
			executionContext.putString(outputKeyName, path + FilenameUtils.getBaseName(inputName)
					+ ".csv");
			
			System.out.println(FilenameUtils.getName(inputName));
			int count = getLineCount(new File(inputName.substring(5, inputName.length())));
		
			executionContext.putInt(FilenameUtils.getBaseName(inputName), count);
			executionContext.putString("cFile",FilenameUtils.getBaseName(inputName) );
		
			
		}
	}
	
	private int getLineCount(File file) throws IOException, FileNotFoundException {
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(file));
            String line = null;
            int count = 0;
            while ((line = lnr.readLine()) != null) {
                count = lnr.getLineNumber();
            }
            return count;
        } finally {
            if (lnr != null) {
                lnr.close();
            }
        }
    }


}
