package com.mobigen.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// @EnableWebSecurity(debug = true)
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

	// https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway.html

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
