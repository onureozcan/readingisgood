package com.example.readingisgood.service.auth;

import com.example.readingisgood.dto.request.AuthenticationRequest;
import com.example.readingisgood.dto.response.AuthenticationResponse;
import com.example.readingisgood.enums.Role;
import com.example.readingisgood.exception.InvalidCredentialsException;
import com.example.readingisgood.model.User;
import com.example.readingisgood.repository.UserRepository;
import com.example.readingisgood.util.PasswordUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private PasswordUtil passwordUtil;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    public void before() {
        mockUser = new User("1", "test@user.com", "name", Role.CUSTOMER.name());
        mockUser.setPasswordSalt("salt");
        mockUser.setPasswordHashed("passwordHashed");

        Mockito.when(userRepository.findUserById(eq(mockUser.getName())))
                .thenReturn(mockUser);

        Mockito.when(passwordUtil.hash(eq("salt"), not(eq("some password"))))
                .thenReturn("someOtherPasswordHashed");

        Mockito.when(passwordUtil.hash(eq("salt"), eq("some password")))
                .thenReturn("passwordHashed");
    }

    @Test
    public void shouldAuthenticateUser() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setPassword("some password");
        request.setEmail(mockUser.getName());

        AuthenticationResponse response = authenticationService.authenticate(
                request
        );

        Assertions.assertAll(
                () -> assertNotNull(response)
        );
    }

    @Test
    public void shouldFailToAuthenticateUserIfNotFound() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setPassword("some password");
        request.setEmail("some nonesense user");

        Assertions.assertThrows(
                InvalidCredentialsException.class, () -> authenticationService.authenticate(request)
        );
    }

    @Test
    public void shouldFailToAuthenticateUserWithInvalidPassword() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setPassword("wrong password");
        request.setEmail(mockUser.getName());

        Assertions.assertThrows(
                InvalidCredentialsException.class, () -> authenticationService.authenticate(request)
        );
    }
}
