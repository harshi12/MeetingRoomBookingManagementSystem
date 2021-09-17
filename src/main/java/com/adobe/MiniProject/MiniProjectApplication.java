package com.adobe.MiniProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MiniProjectApplication {

	public static void main(String[] args) {
		ApplicationContext springContainer = SpringApplication.run(MiniProjectApplication.class, args);
	}
}
