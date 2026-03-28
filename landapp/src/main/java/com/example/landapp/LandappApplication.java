package com.example.landapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LandappApplication {

	public static void main(String[] args) {
		SpringApplication.run(LandappApplication.class, args);
	}

}
