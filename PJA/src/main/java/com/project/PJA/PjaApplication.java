package com.project.PJA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.project.PJA")
public class PjaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PjaApplication.class, args);
	}

}
