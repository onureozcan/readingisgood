package com.example.readingisgood.controller;

import com.example.readingisgood.dto.request.AuthenticationRequest;
import com.example.readingisgood.dto.response.AuthenticationResponse;
import com.example.readingisgood.service.auth.AuthenticationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @ApiOperation("authenticates a user with email and password")
    @PostMapping
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }
}
