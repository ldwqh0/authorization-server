package com.xyyh.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/t")
public class ResourceController {

    @GetMapping
    public String get() {
        return "success";
    }

}
