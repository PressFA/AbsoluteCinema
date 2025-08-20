package org.example.absolutecinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/login")
    public String authentication() {
        return "login";
    }

    @GetMapping("/registration")
    public String authorization() {
        return "registration";
    }
}
