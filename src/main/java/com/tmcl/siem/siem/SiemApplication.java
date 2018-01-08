package com.tmcl.siem.siem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages={"com.tmcl.siem"})
@EnableAutoConfiguration
@EntityScan(basePackages = { "com.tmcl.siem.domain" }) 
@EnableJpaRepositories(basePackages = { "com.tmcl.siem.repo" })
@EnableCaching
@EnableScheduling
@EnableAsync
public class SiemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiemApplication.class, args);
	}
}
