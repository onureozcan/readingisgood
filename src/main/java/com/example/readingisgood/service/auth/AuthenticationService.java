package com.example.readingisgood.service.auth;

import com.example.readingisgood.dto.request.AuthenticationRequest;
import com.example.readingisgood.dto.response.AuthenticationResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        System.out.println("authentication requested" + authenticationRequest.getUserName()
                + "," + authenticationRequest.getPassword());

        return new AuthenticationResponse("token will be here ");
    }
}
