package com.example.flakyclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "flaky", url = "http://localhost:8081")
interface FlakyClient {

  @RequestMapping(method = RequestMethod.GET, value = "/hello/{name}")
  Person greet(@PathVariable("name") String name);
}
