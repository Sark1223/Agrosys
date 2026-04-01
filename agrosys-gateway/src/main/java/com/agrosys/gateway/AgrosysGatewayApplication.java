package com.agrosys.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AgrosysGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgrosysGatewayApplication.class, args);
	}

}
