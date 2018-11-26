package com.gerry.pang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationRabbitMQ {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationRabbitMQ.class, args);
		System.out.println("=====  start success =====");
	}

}
