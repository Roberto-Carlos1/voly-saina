package com.voly_saina;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.voly_saina.properties.StorageProperties;
import com.voly_saina.service.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)	
public class VolySainaApplication {

	public static void main(String[] args) {
		SpringApplication.run(VolySainaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return args -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
