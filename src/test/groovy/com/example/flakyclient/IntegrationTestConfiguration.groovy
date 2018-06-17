package com.example.flakyclient

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import org.springframework.retry.support.RetryTemplate
import org.springframework.web.client.RestTemplate
import spock.mock.DetachedMockFactory

@TestConfiguration
class IntegrationTestConfiguration {

    def detachedMockFactory = new DetachedMockFactory()

    @Bean
    FlakyService flakyService() {
        return detachedMockFactory.Mock(FlakyService)
    }

    @Bean
    RestTemplate restTemplate() {
        return detachedMockFactory.Mock(RestTemplate)
    }

    @Bean
    RetryTemplate retryTemplate() {
        return detachedMockFactory.Mock(RetryTemplate)
    }

    @Bean
    FlakyClient flakyClient() {
        return detachedMockFactory.Mock(FlakyClient)
    }
}
