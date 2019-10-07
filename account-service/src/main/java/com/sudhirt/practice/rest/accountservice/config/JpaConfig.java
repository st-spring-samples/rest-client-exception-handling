package com.sudhirt.practice.rest.accountservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.sudhirt.practice.rest.accountservice")
public class JpaConfig {

}