package com.batch.config;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.batch.listener.StepListener;
import com.batch.mapper.UserInputMapper;
import com.batch.model.UserInput;
import com.batch.model.UserOutput;
import com.batch.processor.UserProcessor;
import com.batch.skip_policy.RecordSkipPolicy;
import com.batch.writer.UserWriter;

@Configuration
public class UserBatchConfig {

	@Value("${userbatch.threadcount}")
	private int threadCount;

	@Value("${userbatch.step-name}")
	private String stepName;

	@Value("${userbatch.chunk-size}")
	private int chunkSize;

	@Value("${userbatch.files-path}")
	private String filesPath;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private UserProcessor userProcessor;
	
	@Autowired
	private RecordSkipPolicy recordSkipPolicy;

	@Autowired
	private UserWriter userWriter;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepListener stepListener;

	public Job runJob() throws IOException {
		return jobBuilderFactory.get("users").incrementer(new RunIdIncrementer()).flow(step()).end().build();
	}

    @Bean
    Step step() throws IOException {
		return stepBuilderFactory.get("masterStep-" + stepName).partitioner(stepName, partitioner())
				.step(partitionedStep()).taskExecutor(taskExecutor()).build();

	}

	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(threadCount);
		return taskExecutor;
	}

	@Bean
	public Step partitionedStep() throws IOException {
		return stepBuilderFactory.get(stepName).<UserInput, UserOutput>chunk(chunkSize)
				.reader(reader(null))
				.faultTolerant()
				.skipPolicy(recordSkipPolicy)
				.processor(userProcessor)
				.writer(userWriter)
				.listener(stepListener)
				.build();
	}

	private Resource[] getResources() throws IOException {
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = patternResolver.getResources("file:" + filesPath);
		return resources;
	}

	@Bean
	public Partitioner partitioner() throws IOException {
		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		partitioner.setResources(getResources());
		partitioner.partition(threadCount);
		return partitioner;
	}

	@Bean
	@StepScope
	FlatFileItemReader<UserInput> reader(@Value("#{stepExecutionContext['fileName']}") String filename)
			throws MalformedURLException {
		FlatFileItemReader<UserInput> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new UrlResource(filename));
		reader.setLineMapper(lineMapper());
		return reader;
	}

	public LineMapper<UserInput> lineMapper() {
		DefaultLineMapper<UserInput> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(getFileHeader());
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper());
		return lineMapper;
	}

	public FieldSetMapper<UserInput> fieldSetMapper() {
		return new UserInputMapper();
	}

	public String[] getFileHeader() {
		return new String[] { "id", "name", "email", "dob", "phone" };
	}

}
