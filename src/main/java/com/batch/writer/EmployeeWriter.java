package com.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.batch.model.EmployeeOutput;

@Component
public class EmployeeWriter implements ItemWriter<EmployeeOutput> {

	@Override
	public void write(List<? extends EmployeeOutput> items) throws Exception {
		items.forEach(e->{
			System.out.println(e.getDateOfBirth());
		});
	}

	

}
