package com.gerry.pang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.gerry.pang.*"})
public class RabbitmqReliableApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqReliableApplication.class, args);
		System.out.println("========== start success =============");
	}
}
