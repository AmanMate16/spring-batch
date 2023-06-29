package com.batch.processor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.model.Employee;
import com.batch.model.EmployeeOutput;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, EmployeeOutput> {

	@Override
	public EmployeeOutput process(Employee item) throws Exception {
		EmployeeOutput out = new EmployeeOutput();
		out.setAge(item.getAge());
		out.setDateOfBirth(getDateOfBirth(item.getAge()));
		out.setEmail(item.getEmail());
		out.setGender(item.getGender().toUpperCase());
		out.setId(item.getId());
		out.setName(item.getName());
		
		return out;
	}
	
	private String getDateOfBirth(int age)
	{
		LocalDate now = LocalDate.now();
		LocalDate dob = now.minusYears(age);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return dob.format(formatter);
	}

}
