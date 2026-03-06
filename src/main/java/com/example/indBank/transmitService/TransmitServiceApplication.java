package com.example.indBank.transmitService;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TransmitServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransmitServiceApplication.class, args);
	}

}
