package com.example.flakyclient

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

@WebMvcTest(controllers = [FlakyClientController])
@Import(IntegrationTestConfiguration)
class FlakyClientControllerSpec extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    FlakyService flakyService

    def "should call the service"() {
        given:
        Person person = new Person("alice")
        1 * flakyService.sayHello("alice") >> person
        String content = "{\"name\":\"alice\"}"

        expect:
        mvc.perform(MockMvcRequestBuilders.get("/greet/alice"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(content))
    }
}
