package com.example.flakyclient;

import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static feign.FeignException.errorStatus;

@Configuration
@EnableFeignClients
public class FlakyClientConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return (methodKey, response) -> {
      if (response.status() >= 400 && response.status() <= 499) {
        throw new HttpClientErrorException(HttpStatus.valueOf(response.status()));
      }
      return errorStatus(methodKey, response);
    };
  }


}
