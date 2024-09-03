package org.camunda.consulting.tasklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomTasklistIncludingAuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomTasklistIncludingAuthorizationApplication.class, args);
	}

}
