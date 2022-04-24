package com.example.readingisgood.repository;

import com.example.readingisgood.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public User findUserById(String email) {
        // TODO
        return null;
    }

    public void save(User user) {

        // TODO
    }
}
