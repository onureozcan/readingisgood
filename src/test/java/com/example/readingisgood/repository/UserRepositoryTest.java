package com.example.readingisgood.repository;

import com.example.readingisgood.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateNewUser() {
        User newUser = getNewUser();
        userRepository.save(newUser);

        User found = userRepository.findUserById("user@user.com");
        assertUser(newUser, found);
    }

    @Test
    public void testUpdateNewUser() {
        User newUser = getNewUser();
        userRepository.save(newUser);

        newUser.setRole("MANAGER");
        newUser.setName("Updated");
        userRepository.save(newUser);

        User found = userRepository.findUserById("user@user.com");
        assertUser(newUser, found);
    }

    private User getNewUser() {
        User newUser = new User(
                "user@user.com", "user@user.com", "User", "CUSTOMER"
        );
        newUser.setPasswordHashed("some random thing");
        newUser.setPasswordSalt("salty");
        return newUser;
    }

    private void assertUser(User newUser, User found) {
        assertAll(
                () -> assertNotNull(found),
                () -> assertEquals(newUser.getName(), found.getName()),
                () -> assertEquals(newUser.getRole(), found.getRole()),
                () -> assertEquals(newUser.getId(), found.getId()),
                () -> assertEquals(newUser.getPasswordHashed(), found.getPasswordHashed()),
                () -> assertEquals(newUser.getPasswordSalt(), found.getPasswordSalt())
        );
    }
}
