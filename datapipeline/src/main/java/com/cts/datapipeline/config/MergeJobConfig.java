package com.cts.datapipeline.config;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;

@Configuration
@EnableBatchProcessing
public class MergeJobConfig {
	@Bean
	@StepScope
	public MultiResourceItemReader<String> mergeItemReader(
			@Value("#{jobExecutionContext[jobDataPath]}")  String inputDirLocation) throws Exception {
		FlatFileItemReader<String> delegate = new FlatFileItemReader<String>();
		delegate.setLineMapper(new PassThroughLineMapper());
		delegate.afterPropertiesSet();
        System.out.println("Job path --->" + inputDirLocation);
		MultiResourceItemReader<String> reader = new MultiResourceItemReader<>();
		reader.setDelegate(delegate);
		reader.setResources(getResources(inputDirLocation));

		return reader;
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<String> mergerWriter(@Value("#{jobParameters[mergeOutputDirLocation]}") String outputDirLocation,@Value("#{stepExecution}") StepExecution stepExecution) throws Exception {
		FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
		writer.setLineAggregator(new PassThroughLineAggregator<>());
		//writer.setStepExecution(stepExecution);
		writer.setResource(new FileSystemResource(outputDirLocation));
		return writer;
	}

	private Resource[] getResources(String stagingDirectory) {
		ResourceArrayPropertyEditor resourceLoader = new ResourceArrayPropertyEditor();
		resourceLoader.setAsText("file:"+stagingDirectory + "/*");
		Resource[] resources = (Resource[]) resourceLoader.getValue();
		return resources;
	}
}
