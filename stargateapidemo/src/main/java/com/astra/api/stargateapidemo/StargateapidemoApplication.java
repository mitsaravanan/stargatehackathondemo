package com.astra.api.stargateapidemo;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class StargateapidemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StargateapidemoApplication.class, args);
	}
	
   @Bean
   public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(120))
            .setReadTimeout(Duration.ofSeconds(120))
            .build();
   }

}
