package com.javalife365.authify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AuthifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthifyApplication.class, args);
	}

}
