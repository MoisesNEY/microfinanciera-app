package com.microfinanciera.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HomeController {

    @GetMapping("/hello")
    public String hello(){
        return "Microfinanciera API OK";
    }
    
}
