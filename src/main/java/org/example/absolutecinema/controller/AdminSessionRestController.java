package org.example.absolutecinema.controller;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.service.SessionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/sessions")
public class AdminSessionRestController {
    private final SessionService sessionService;
}
