package com.example.readingisgood.mapper;

import com.example.readingisgood.dto.request.CreateUserRequest;
import com.example.readingisgood.enums.Role;
import com.example.readingisgood.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toCustomer(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setRole(Role.CUSTOMER.name());
        user.setEmail(request.getEmail());
        user.setId(request.getEmail());
        return user;
    }
}
