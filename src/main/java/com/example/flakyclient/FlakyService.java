package com.example.flakyclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class FlakyService {

  private static final Log LOG = LogFactory.getLog(FlakyService.class);

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private RetryTemplate retryTemplate;

  @Autowired
  private FlakyClient flakyClient;

  @Value("${flaky.server.host}")
  private String endpoint;

  @Value("${flaky.server.port}")
  private String port;

  public FlakyService(RestTemplate restTemplate,
                      RetryTemplate retryTemplate,
                      FlakyClient flakyClient) {
    this.restTemplate = restTemplate;
    this.retryTemplate = retryTemplate;
    this.flakyClient = flakyClient;
  }

  @HystrixCommand(fallbackMethod = "fallback")
  public Person sayHello(String name) {
    try {
      return doRemoteCall(name);
    }
    catch (HttpClientErrorException e) {
      throw new HystrixBadRequestException("Bad request", e);
    }
  }

  public Person fallback(String name) {
    return new Person("stranger");
  }

  @HystrixCommand
  public Person feignHello(String name) {
    try {
      return callfeign(name);
    }
    catch (HttpClientErrorException e) {
      throw new HystrixBadRequestException("Bad request", e);
    }
  }

  public Person doRemoteCall(String name) {
    return retryTemplate.execute(arg -> {
      LOG.info("doing remote call");
          return restTemplate.getForObject(
        String.format("http://%s:%s/hello/%s", endpoint, port, name),
        Person.class);
    });
  }

  public Person callfeign(String name) {
    return retryTemplate.execute(arg -> {
      LOG.info("doing remote call");
      return flakyClient.greet(name);
    });
  }

}
