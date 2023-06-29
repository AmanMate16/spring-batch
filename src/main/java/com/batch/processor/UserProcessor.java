package com.batch.processor;

import java.util.UUID;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.model.UserInput;
import com.batch.model.UserOutput;

@Component
public class UserProcessor implements ItemProcessor<UserInput, UserOutput> {

	@Override
	public UserOutput process(UserInput item) throws Exception {
		UserOutput output=new UserOutput();
		output.setEmail(item.getEmail());
		output.setFullName(item.getName());
		output.setId(Long.valueOf(item.getId()));
		output.setUuid(UUID.randomUUID().toString());
		return output;
	}

}
