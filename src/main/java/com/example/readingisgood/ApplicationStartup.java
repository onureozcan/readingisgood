package com.example.readingisgood;

import com.example.readingisgood.dto.request.CreateUserRequest;
import com.example.readingisgood.exception.UserAlreadyPresentException;
import com.example.readingisgood.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(ApplicationStartup.class.getName());

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        try {
            log.info("creating admin user");
            userService.createRootAdmin(new CreateUserRequest("admin@admin.com", "admin"), "admin");
        } catch (UserAlreadyPresentException exception) {
            // Ignored
            log.info("already created, skipping");
        }
    }
}