package com.xyyh.authorization.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping
    public String test(Principal principal) {
        System.out.println(principal);
        return "test";
    }
}
