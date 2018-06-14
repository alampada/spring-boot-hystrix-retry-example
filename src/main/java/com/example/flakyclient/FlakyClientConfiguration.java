package com.example.flakyclient;

import feign.codec.ErrorDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static feign.FeignException.errorStatus;

@Configuration
public class FlakyClientConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public RetryListener retryListener() {
    return new RetryListener() {
      private Log log = LogFactory.getLog(RetryListener.class);
      @Override
      public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return true;
      }

      @Override
      public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {

      }

      @Override
      public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
         log.warn("failed attempt: " + context.getRetryCount() + " with  " + throwable.getMessage());
      }
    };
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

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(500l);
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
    final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(2);

    ExceptionClassifierRetryPolicy exceptionClassifierRetryPolicy = new ExceptionClassifierRetryPolicy();
    exceptionClassifierRetryPolicy.setExceptionClassifier(
        (Classifier<Throwable, RetryPolicy>) classifiable -> {
          if (classifiable instanceof HttpClientErrorException) {
            return new NeverRetryPolicy();
          }
          return retryPolicy;
        }
    );
    retryTemplate.setListeners(new RetryListener[] {retryListener()});
    retryTemplate.setRetryPolicy(exceptionClassifierRetryPolicy);
    return retryTemplate;
  }
}
