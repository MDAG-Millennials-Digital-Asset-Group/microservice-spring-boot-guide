package com.example.springcicddemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@RestController
public class App {

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@GetMapping("/hello")
	public String hello(){
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return String.format("host-address: %s host-name: %s",
					localHost.getHostAddress(),
					localHost.getHostName());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}
