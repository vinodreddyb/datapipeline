package com.cts.datapipeline.config;

import javax.sql.DataSource;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class CommonConfig {
	
	@Bean(name = "dataSource")
    @ConfigurationProperties(prefix="spring.datasource")
	public DataSource dataSource() throws Exception {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager() throws Exception {
		return new DataSourceTransactionManager(dataSource());
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
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher launch = new SimpleJobLauncher();
		launch.setJobRepository(jobRepository());
		launch.afterPropertiesSet();
		return launch;
	}
	
	/*
	private DatabasePopulator databasePopulator() {
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScripts(schemaScript);
		return populator;
	}*/

	


	

}
