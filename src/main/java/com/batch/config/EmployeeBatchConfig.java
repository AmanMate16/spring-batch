package com.batch.config;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.batch.mapper.EmployeeInputMapper;
import com.batch.model.Employee;
import com.batch.model.EmployeeOutput;
import com.batch.processor.EmployeeProcessor;
import com.batch.writer.EmployeeWriter;

@Configuration
public class EmployeeBatchConfig {

	@Value("${employee-batch.step-name}")
	private String stepName;

	@Value("${employee-batch.chunk-size}")
	private int chunkSize;

	@Value("${employee-batch.files-path}")
	private String filesPath;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private EmployeeProcessor employeeProcessor;

	@Autowired
	private EmployeeWriter employeeWriter;
	
	public Job runJob() throws IOException {
		return jobBuilderFactory.get("employee").incrementer(new RunIdIncrementer()).flow(employeeStep()).end().build();
	}

    @Bean
    Step employeeStep() throws IOException {
		return stepBuilderFactory
				.get(stepName)
				.<Employee,EmployeeOutput>chunk(chunkSize)
				.reader(multiResourceItemreader())
				.processor(employeeProcessor)
				.writer(employeeWriter)
				.build();

	}
    
	@Bean
	public MultiResourceItemReader<Employee> multiResourceItemreader() throws IOException {
		MultiResourceItemReader<Employee> reader = new MultiResourceItemReader<>();
		reader.setDelegate(employeeReader());
		reader.setResources(getResources());
		return reader;
	}
    
    private Resource[] getResources() throws IOException {
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = patternResolver.getResources("file:" + filesPath);
		return resources;
	}
    
	@Bean
	@StepScope
	FlatFileItemReader<Employee> employeeReader()
			throws MalformedURLException {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setLineMapper(lineMapper());
		return reader;
	}

	public LineMapper<Employee> lineMapper() {
		DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(getFileHeader());
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper());
		return lineMapper;
	}

	public FieldSetMapper<Employee> fieldSetMapper() {
		return new EmployeeInputMapper();
	}

	public String[] getFileHeader() {
		return new String[] { "id", "name", "email", "gender", "age" };
	}
	
	
}
