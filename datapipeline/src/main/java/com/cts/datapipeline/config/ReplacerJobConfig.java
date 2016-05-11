package com.cts.datapipeline.config;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;

import com.cts.datapipeline.job.listeners.OutputFileListener;

@Configuration
@EnableBatchProcessing
public class ReplacerJobConfig {

	
	@Bean
	@StepScope
	public MultiResourcePartitioner replacerPartitionReader(@Value("#{jobParameters[inputDirLocation]}") String input) {
		
		MultiResourcePartitioner partition = new MultiResourcePartitioner();
		partition.setResources(getResources(input));
		return partition;
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<String> replacerItemReader(@Value("#{stepExecution}") StepExecution stepExecution,
			@Value("#{stepExecutionContext[fileName]}") Resource resource) {
		FlatFileItemReader<String> reader = new FlatFileItemReader<>();
		reader.setResource(resource);
		reader.setLineMapper(new com.cts.datapipeline.job.chunks.PassThroughLineMapper(stepExecution));
		return reader;
	}
	
	@Bean
	@StepScope
	public FlatFileItemWriter<String> replacerItemWriter(@Value("#{stepExecutionContext[outputFile]}") Resource resource ) {
		FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
		writer.setLineAggregator(new PassThroughLineAggregator<>());
		writer.setResource(resource);
		return writer;
	}
	
	@Bean
	@StepScope
	public OutputFileListener outputFileListener(@Value("#{jobParameters[outputDirLocation]}") String output) {
		OutputFileListener listen = new OutputFileListener();
		listen.setPath(output);
		return listen;
	}

	private Resource[] getResources(String stagingDirectory) {
		ResourceArrayPropertyEditor resourceLoader = new ResourceArrayPropertyEditor();
		resourceLoader.setAsText("file:" + stagingDirectory + "/*");
		Resource[] resources = (Resource[]) resourceLoader.getValue();
		return resources;
	}
}
