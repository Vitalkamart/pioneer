package ru.mart.pioneer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {
    @GetMapping("/swagger")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
