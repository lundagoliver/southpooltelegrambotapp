package com.systems.community.carpooling.southpool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaRepositories
@ComponentScan(basePackages = { "com.systems.community.carpooling.southpool" })
@EntityScan(basePackages = { "com.systems.community.carpooling.southpool.entities" })
public class SouthpoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(SouthpoolApplication.class, args);
	}

}

