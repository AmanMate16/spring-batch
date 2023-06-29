package com.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.batch.model.UserOutput;

@Component
public class UserWriter implements ItemWriter<UserOutput> {

	
	@Override
	public void write(List<? extends UserOutput> items) throws Exception {
		items.forEach(item->{
			System.out.println(item.getFullName()+" "+item.getUuid());
		});
	}

}
