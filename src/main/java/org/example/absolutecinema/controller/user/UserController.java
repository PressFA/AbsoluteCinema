package org.example.absolutecinema.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/login")
    public String authentication() {
        return "login";
    }

    @GetMapping("/registration")
    public String authorization() {
        return "registration";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
