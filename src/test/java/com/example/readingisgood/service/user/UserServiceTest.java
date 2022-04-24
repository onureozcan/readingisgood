package com.example.readingisgood.service.user;


import com.example.readingisgood.constant.PasswordConstants;
import com.example.readingisgood.dto.request.CreateUserRequest;
import com.example.readingisgood.exception.UserAlreadyPresentException;
import com.example.readingisgood.model.User;
import com.example.readingisgood.repository.UserRepository;
import com.example.readingisgood.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private PasswordUtil passwordUtil;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void before() {
        Mockito.when(passwordUtil.generateSalt(eq(PasswordConstants.SALT_SIZE))).thenReturn("salt");
        Mockito.when(passwordUtil.generateFirstTimePassword(eq(PasswordConstants.FIRST_TIME_PASSWORD_SIZE))).thenReturn("password");
        Mockito.when(passwordUtil.hash(eq("salt"), eq("password"))).thenReturn("hashed");
    }

    @ParameterizedTest
    @CsvSource({"user@user.com, false", "manager@manager.com, true"})
    public void shouldCreateNewUser(String email, boolean isManager) {

        Mockito.when(userRepository.findUserById(eq(email))).thenReturn(Optional.empty());

        CreateUserRequest request = new CreateUserRequest(email, "user");
        if (isManager) {
            userService.createManager(request);
        } else {
            userService.createCustomer(request);
        }
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, times(1)).save(argumentCaptor.capture());

        User savedUser = argumentCaptor.getValue();

        assertAll(
                () -> assertEquals("user", savedUser.getName()),
                () -> assertEquals(email, savedUser.getEmail()),
                () -> assertEquals("hashed", savedUser.getPasswordHashed()),
                () -> assertEquals("salt", savedUser.getPasswordSalt()),
                () -> assertEquals(isManager ? "MANAGER" : "CUSTOMER", savedUser.getRole())
        );
    }

    @Test
    public void shouldFailToCreateNewCustomerIfExists() {

        String email = "user@user.com";
        Mockito.when(userRepository.findUserById(eq(email))).thenReturn(Optional.of(new User()));

        CreateUserRequest request = new CreateUserRequest(email, "user");

        assertThrows(UserAlreadyPresentException.class, () -> userService.createCustomer(request));
    }
}
