package com.example.flakyclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableHystrix
@EnableRetry
@EnableFeignClients
public class FlakyClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlakyClientApplication.class, args);
	}
}
