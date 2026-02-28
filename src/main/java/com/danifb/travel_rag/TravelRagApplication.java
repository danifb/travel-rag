package com.danifb.travel_rag;

import com.danifb.travel_rag.config.OpenAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenAiProperties.class)
public class TravelRagApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelRagApplication.class, args);
	}

}
