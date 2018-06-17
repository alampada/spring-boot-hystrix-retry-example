package com.example.flakyclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(FlakyClientController.class)
public class FlakyClientControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private FlakyService flakyService;

  @Test
  public void greetShouldReturnGreeting() throws Exception {
    String content = "{\"name\":\"alice\"}";

    given(flakyService.sayHello("alice")).willReturn(new Person("alice"));

    this.mvc.perform(MockMvcRequestBuilders.get("/greet/alice"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(content));
  }

}
