package com.example.readingisgood.repository;

import com.example.readingisgood.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public User findUserById(String email) {
        // TODO
        return new User("onureozcan@gmail.com", "onureozcan@gmail.com","onur","CUSTOMER");
    }
}
