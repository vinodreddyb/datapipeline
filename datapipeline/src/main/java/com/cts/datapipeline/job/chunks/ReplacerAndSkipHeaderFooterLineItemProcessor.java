/**
 * Copyright 2011 Michael R. Lange <michael.r.lange@langmi.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cts.datapipeline.job.chunks;

import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;

import com.cts.datapipeline.input.constants.ReplacerInputParams;

public class ReplacerAndSkipHeaderFooterLineItemProcessor implements ItemProcessor<String, String> {

	private StepExecution stepExecution;
	private Map<String,Object> parameters;
	
	public ReplacerAndSkipHeaderFooterLineItemProcessor(StepExecution stepExecution,Map<String, Object> parameters) {
		this.stepExecution = stepExecution;
		this.parameters = parameters;
	}
    @Override
    public String process(String line) throws Exception {
    	
    	//int count = (int) stepExecution.getExecutionContext().get("line.count");
    	//System.out.println("TT Count--->" + count);
    	int totalRows = -1;
    	int currentLine = -1;
		if (stepExecution.getExecutionContext().containsKey("cFile")) {
			String cFile = stepExecution.getExecutionContext().getString("cFile");
			totalRows = (int) stepExecution.getExecutionContext().get(cFile);
			currentLine =  stepExecution.getExecutionContext().getInt("currentLine");
			System.out.println(cFile + " " + totalRows);
		}
    	
		boolean isPatternBased = Boolean.parseBoolean( (String) parameters.get(ReplacerInputParams.header_PatternBased.name()));
		boolean isRecordBased = Boolean.parseBoolean( (String)parameters.get(ReplacerInputParams.header_RecordBased.name()));
		
		if(isPatternBased) {
			String header = (String) parameters.get(ReplacerInputParams.header.name());
			String footer = (String) parameters.get(ReplacerInputParams.footer.name());
			

	        if (line.contains(header) || line.contains(footer)) {
	            return null;
	        } else {
	            return line;
	        }
		} else if(isRecordBased) {
			long headerRows = (long) parameters.get(ReplacerInputParams.headerRows.name());
			long footerRows = (long) parameters.get(ReplacerInputParams.footerRows.name());
			
			if(currentLine <= headerRows) {
				return null;
			} 
			
			if(currentLine > totalRows - footerRows) {
				return null;
			}
		}
		
		String find = (String) parameters.get(ReplacerInputParams.find.name());
		String replace = (String) parameters.get(ReplacerInputParams.replace.name());
		return line.replace(find, replace);
    }
	public StepExecution getStepExecution() {
		return stepExecution;
	}
	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
}
