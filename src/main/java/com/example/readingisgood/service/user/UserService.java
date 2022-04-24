package com.example.readingisgood.service.user;

import com.example.readingisgood.dto.request.CreateUserRequest;
import com.example.readingisgood.enums.Role;
import com.example.readingisgood.exception.UserAlreadyPresentException;
import com.example.readingisgood.mapper.UserMapper;
import com.example.readingisgood.model.User;
import com.example.readingisgood.repository.UserRepository;
import com.example.readingisgood.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import static com.example.readingisgood.constant.PasswordConstants.FIRST_TIME_PASSWORD_SIZE;
import static com.example.readingisgood.constant.PasswordConstants.SALT_SIZE;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordUtil passwordUtil;

    private static final Logger log = LoggerFactory.getLogger(UserService.class.getName());

    public User createCustomer(CreateUserRequest request) {
        return saveUser(request, Role.CUSTOMER, null);
    }

    public User createManager(CreateUserRequest request) {
        return saveUser(request, Role.MANAGER, null);
    }

    public User createRootAdmin(CreateUserRequest request, String password) {
        return saveUser(request, Role.MANAGER, password);
    }

    private User saveUser(CreateUserRequest request, Role role, @Nullable String explicitPassword) {
        String email = request.getEmail();

        if (userRepository.findUserById(request.getEmail()).isPresent()) {
            throw new UserAlreadyPresentException(email);
        }

        User user = userMapper.toUser(request, role);
        user.setPasswordSalt(passwordUtil.generateSalt(SALT_SIZE));
        String password = explicitPassword == null ?
                passwordUtil.generateFirstTimePassword(FIRST_TIME_PASSWORD_SIZE) : explicitPassword;

        // Only to be able to see
        log.info("Created a first time password: " + password);

        user.setPasswordHashed(
                passwordUtil.hash(
                        user.getPasswordSalt(),
                        password
                )
        );
        return userRepository.save(user);
    }
}
