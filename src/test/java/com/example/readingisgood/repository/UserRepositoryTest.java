package com.example.readingisgood.repository;

import com.example.readingisgood.TestMongoDb;
import com.example.readingisgood.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        TestMongoDb.start();
    }

    @AfterAll
    public static void afterAll() {
        TestMongoDb.stop();
    }

    @Test
    public void testCreateNewUser() {
        User newUser = getNewUser();
        userRepository.save(newUser);

        User found = userRepository.findUserById("user@user.com").orElseThrow();
        assertUser(newUser, found);
    }

    @Test
    public void testUpdateNewUser() {
        User newUser = getNewUser();
        userRepository.save(newUser);

        newUser.setRole("MANAGER");
        newUser.setName("Updated");
        userRepository.save(newUser);

        User found = userRepository.findUserById("user@user.com").orElseThrow();
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
