package com.nguyen.fashion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FashionBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(FashionBotApplication.class, args);
	}

}
