package com.example.readingisgood.service.auth;

import com.example.readingisgood.dto.request.AuthenticationRequest;
import com.example.readingisgood.dto.response.AuthenticationResponse;
import com.example.readingisgood.exception.InvalidCredentialsException;
import com.example.readingisgood.mapper.AuthenticationMapper;
import com.example.readingisgood.model.User;
import com.example.readingisgood.repository.UserRepository;
import com.example.readingisgood.util.JwtGenerator;
import com.example.readingisgood.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        Optional<User> userOptional = userRepository.findUserById(authenticationRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String hashedPassword = passwordUtil.hash(user.getPasswordSalt(), authenticationRequest.getPassword());
            String expectedHashedPassword = user.getPasswordHashed();

            if (hashedPassword.equals(expectedHashedPassword)) {
                return new AuthenticationResponse(jwtGenerator.generateJwt(
                        authenticationMapper.toPayload(user)
                ));
            } else {
                user.setLastFailedLoginAt(Instant.now());
                userRepository.save(user);
            }
        }
        throw new InvalidCredentialsException();
    }
}
