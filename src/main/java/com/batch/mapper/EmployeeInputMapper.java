package com.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.batch.model.Employee;

public class EmployeeInputMapper implements FieldSetMapper<Employee>  {

	@Override
	public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
		Employee employee = new Employee();
		employee.setAge(fieldSet.readInt("age"));
		employee.setEmail(fieldSet.readString("email"));
		employee.setGender(fieldSet.readString("gender"));
		employee.setId(fieldSet.readLong("id"));
		employee.setName(fieldSet.readString("name"));
		
		return employee;
	}


}
