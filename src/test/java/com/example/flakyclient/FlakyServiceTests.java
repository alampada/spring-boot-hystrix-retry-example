package com.example.flakyclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class FlakyServiceTests {

  @Autowired
  private FlakyService service;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void shouldRespondWithPerson() {
    mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost:9000/hello/alice"))
        .andRespond(withSuccess(new ClassPathResource("hello.json", getClass()), MediaType.APPLICATION_JSON));

    Person result = service.sayHello("alice");
    assertEquals(new Person("alice"), result);
  }

  @Test
  public void shouldRetryServerErrors() {
    mockServer.expect(ExpectedCount.twice(),
        MockRestRequestMatchers.requestTo("http://localhost:9000/hello/alice"))
        .andRespond(withServerError());

    Person result = service.sayHello("alice");
    assertEquals(new Person("stranger"), result);
    mockServer.verify();
  }

  @Test(expected = HttpClientErrorException.class)
  public void shouldNotRetryBadRequests() {
    mockServer.expect(ExpectedCount.once(),
        MockRestRequestMatchers.requestTo("http://localhost:9000/hello/alice"))
        .andRespond(withBadRequest());
    try {
      Person result = service.sayHello("alice");
    }
    catch (Exception e) {
      mockServer.verify();
      throw e;
    }
  }
}
