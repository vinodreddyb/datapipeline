package com.cts.datapipeline;

import javax.sql.DataSource;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@EnableBatchProcessing
public class ReplacerJobConfig {

	@Value("classpath:schema-mysql.sql")
	private Resource[] schemaScript;

	/*
	 * @Bean public DataSource dataSource() { SimpleDriverDataSource dataSource
	 * = new SimpleDriverDataSource(); dataSource.set
	 * dataSource.setUrl("jdbc:hsqldb:mem:mydb"); dataSource.setUsername("sa");
	 * dataSource.setPassword(""); DataSourceBuilder dataSourceBuilder =
	 * DataSourceBuilder.create();
	 * dataSourceBuilder.url("jdbc:mysql://localhost:3306/datapipeline");
	 * dataSourceBuilder.username("root"); dataSourceBuilder.password("root");
	 * dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver"); return
	 * dataSourceBuilder.build(); }
	 */
	@Bean
	public DataSource dataSource() throws Exception {
		
		final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriver(new com.mysql.jdbc.Driver());
		dataSource.setUrl("jdbc:mysql://localhost:3306/di");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		//DatabasePopulatorUtils.execute(databasePopulator(), dataSource);
		return dataSource;
	}
	
	@Bean
	@StepScope
	public MultiResourcePartitioner mergeFilePartitioner(@Value("#{jobParameters[inputDirLocation]}") String input) {
		
		MultiResourcePartitioner partition = new MultiResourcePartitioner();
		partition.setResources(getResources(input));
		return partition;
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<String> itemReader(@Value("#{stepExecution}") StepExecution stepExecution,
			@Value("#{stepExecutionContext[fileName]}") Resource resource) {
		FlatFileItemReader<String> reader = new FlatFileItemReader<>();
		reader.setResource(resource);
		reader.setLineMapper(new com.cts.datapipeline.PassThroughLineMapper(stepExecution));
		return reader;
	}
	
	@Bean
	@StepScope
	public FlatFileItemWriter<String> itemWriter(@Value("#{stepExecutionContext[outputFile]}") Resource resource ) {
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
	
	

	private DatabasePopulator databasePopulator() {
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScripts(schemaScript);
		return populator;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() throws Exception {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher launch = new SimpleJobLauncher();
		launch.setJobRepository(jobRepository());
		launch.afterPropertiesSet();
		return launch;
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean repo = new JobRepositoryFactoryBean();
		repo.setTransactionManager(transactionManager());
		repo.setDataSource(dataSource());
		repo.setDatabaseType("mysql");
		return (JobRepository) repo.getObject();
	}

	@Bean
	@StepScope
	public MultiResourceItemReader<String> multResourceItemReaderReplace(
			@Value("#{stepExecution}") StepExecution stepExecution,@Value("#{jobParameters[inputDirLocation]}") String inputDirLocation) throws Exception {
		FlatFileItemReader<String> delegate = new FlatFileItemReader<String>();
		delegate.setLineMapper(new com.cts.datapipeline.PassThroughLineMapper(stepExecution));
		delegate.afterPropertiesSet();

		MultiResourceItemReader<String> reader = new MultiResourceItemReader<>();
		reader.setDelegate(delegate);
		reader.setResources(getResources(inputDirLocation));

		return reader;
	}

	@Bean
	@StepScope
	public ReplacerItemWriter<String> mergerWriter(@Value("#{jobParameters[outputDirLocation]}") String outputDirLocation,@Value("#{stepExecution}") StepExecution stepExecution) throws Exception {
		ReplacerItemWriter<String> writer = new ReplacerItemWriter<String>();
		writer.setLineAggregator(new PassThroughLineAggregator<>());
		writer.setStepExecution(stepExecution);
		writer.setResource(new FileSystemResource(outputDirLocation));
		return writer;
	}

	private Resource[] getResources(String stagingDirectory) {
		ResourceArrayPropertyEditor resourceLoader = new ResourceArrayPropertyEditor();
		resourceLoader.setAsText("file:" + stagingDirectory + "/*");
		Resource[] resources = (Resource[]) resourceLoader.getValue();
		return resources;
	}
}
