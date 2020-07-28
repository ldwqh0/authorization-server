package com.xyyh.resource.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/t")
public class ResourceController {

    @GetMapping
    public String get(@AuthenticationPrincipal Object b) {
        System.out.println(b);
        return "success";
    }

}
