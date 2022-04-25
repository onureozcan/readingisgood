package com.example.readingisgood.controller;

import com.example.readingisgood.dto.request.CreateUserRequest;
import com.example.readingisgood.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation("Creates a new customer")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCustomer(@RequestBody CreateUserRequest request) {
        userService.createCustomer(request);
    }
}
