package com.example.flakyclient

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.retry.support.RetryTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class FlakyServiceSpec extends Specification {

    @Autowired
    RestTemplate restTemplate

    MockRestServiceServer mockRestServiceServer

    @Autowired
    FlakyService flakyService

    def setup() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate)
    }

    def "should handle successful response" () {
        given:
        mockRestServiceServer.expect(
                MockRestRequestMatchers.requestTo("http://localhost:9000/hello/alice"))
                .andRespond(withSuccess(new ClassPathResource("hello.json", getClass()), MediaType.APPLICATION_JSON))

        when:
        Person result = flakyService.sayHello("alice")

        then:
        mockRestServiceServer.verify()
        result == new Person("alice")
    }

    def "should retry server errors" () {
        given:
        mockRestServiceServer.expect(ExpectedCount.twice(),
                MockRestRequestMatchers.requestTo("http://localhost:9000/hello/alice"))
                .andRespond(withServerError())

        when:
        Person result = flakyService.sayHello("alice")

        then:
        HttpServerErrorException ex = thrown()
        mockRestServiceServer.verify()
    }


    def "should not retry bad requests" () {
        given:
        mockRestServiceServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo("http://localhost:9000/hello/alice"))
                .andRespond(withBadRequest())

        when:
        Person result = flakyService.sayHello("alice")

        then:
        HttpClientErrorException ex = thrown()
        mockRestServiceServer.verify()
    }
}
