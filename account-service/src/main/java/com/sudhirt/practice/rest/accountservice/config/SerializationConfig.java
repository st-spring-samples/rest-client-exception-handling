package com.sudhirt.practice.rest.accountservice.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SerializationConfig implements WebMvcConfigurer {

	@Bean
	public Hibernate5Module hibernate5Module() {
		return new Hibernate5Module();
	}

}