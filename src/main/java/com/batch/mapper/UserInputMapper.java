package com.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.batch.model.UserInput;

public class UserInputMapper implements FieldSetMapper<UserInput> {

	@Override
	public UserInput mapFieldSet(FieldSet fieldSet)  {
		
		try
		{
			UserInput userInput=new UserInput();
			
			userInput.setId(fieldSet.readInt("id"));
			
			userInput.setEmail(fieldSet.readString("email"));
			
			userInput.setName(fieldSet.readString("name"));
			
			userInput.setDob(fieldSet.readString("dob"));
			
			userInput.setPhone(fieldSet.readString("phone"));
			
			return userInput;

		}catch(Exception ex)
		{
			throw new RuntimeException("Error while reading user input ");
		}
		
	}

	
}
