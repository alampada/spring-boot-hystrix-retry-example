package com.example.flakyclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FlakyClientController {

  @Autowired
  private FlakyService flakyService;

  public FlakyClientController(FlakyService flakyService) {
    this.flakyService = flakyService;
  }

  @GetMapping("/greet/{name}")
  public Person hello(@PathVariable String name) {
    return flakyService.sayHello(name);
  }

  @GetMapping("/feign/{name}")
  public Person helloFeign(@PathVariable String name) {
    return flakyService.feignHello(name);
  }
}
