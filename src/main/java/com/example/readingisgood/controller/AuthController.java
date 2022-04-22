package com.example.readingisgood.controller;

import com.example.readingisgood.dto.request.AuthenticationRequest;
import com.example.readingisgood.service.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public void authenticate(AuthenticationRequest request) {
        authenticationService.authenticate(request);
    }
}
