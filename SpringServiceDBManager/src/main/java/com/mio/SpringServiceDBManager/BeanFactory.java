package com.mio.SpringServiceDBManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mio.SpringServiceDBManager.table.operation.EntityOperation;

@Configuration
public class BeanFactory {

	@Bean
	public static EntityOperation makeEntityOperation() {
		return new EntityOperation();
	}
}
