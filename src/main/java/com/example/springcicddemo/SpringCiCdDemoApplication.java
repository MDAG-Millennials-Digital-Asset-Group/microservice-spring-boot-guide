package com.example.springcicddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringCiCdDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCiCdDemoApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(){
		return "hello world";
	}
}
