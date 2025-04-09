package com.example.vibely_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.vibely_backend.config.CloudinaryProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableConfigurationProperties(CloudinaryProperties.class)
@EnableMongoAuditing
public class VibelyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VibelyBackendApplication.class, args);
	}

}
