package com.example.readingisgood.factory;

import com.example.readingisgood.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserAuthenticationFactoryTest {

    @Autowired
    private UserAuthenticationFactory userAuthenticationFactory;

    @Test
    public void shouldGenerateAuthentication() {
        String testToken = "eyJhbGciOiJIUzUxMiJ9.eyJwYXlsb2FkIjoie1wiaWRcIjpcIm9udXJlb3pjYW5AZ21haWwuY29tXCIsXCJuYW1lXCI6XCJvbnVyXCIsXCJlbWFpbFwiOlwib251cmVvemNhbkBnbWFpbC5jb21cIixcInJvbGVzXCI6W1wiQ1VTVE9NRVJcIl19IiwiaXNzIjoicmVhZGluZy1pcy1nb29kIn0.1sqaw5QFV3JtHfLXexiVEnaaQWfcHJa10kPkwQYxV-OSRwhg_T9z0C3pCsuRYVQJZOkJ3Twqw0fBah5Z-MG7xw";
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + testToken);

        Authentication authentication = userAuthenticationFactory.getAuthentication(
                servletRequest
        );

        assertAll(
                () -> assertEquals("onureozcan@gmail.com", authentication.getPrincipal()),
                () -> assertEquals("onureozcan@gmail.com", authentication.getDetails()),
                () -> assertEquals("onur", authentication.getName()),
                () -> assertEquals("ROLE_" + Role.CUSTOMER.name(),
                        authentication.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .findFirst().orElseThrow()
                )
        );
    }
}
