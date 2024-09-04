package com.myfinbank.service;

import com.myfinbank.entity.User;
import com.myfinbank.entity.UserLogin;
import com.myfinbank.repository.UserLoginRepository;
import com.myfinbank.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private UserRepository userRepository;

    private UserLoginRepository userLoginRepository;

    public AuthService(UserRepository userRepository, UserLoginRepository userLoginRepository) {
        this.userRepository = userRepository;
        this.userLoginRepository = userLoginRepository;
    }

    // login

        // logout
        public boolean logout(String username) {
            Optional<UserLogin> findUserLogin = userLoginRepository.findByUsername(username);
            if (findUserLogin.isPresent()) {
                logger.info("User logged in at: {}", findUserLogin.get().getLoginTime());
                UserLogin userLogin = findUserLogin.get();
                if (userLogin.getLogoutTime() != null) {
                    logger.error("User already logged out: {}", username);
                    return true;
                }
                userLogin.setLogoutTime(LocalDateTime.now());
                userLoginRepository.save(userLogin);
                return true;
            } else {
                logger.error("User not found: {}", username);
                return false;
            }
        }
        // register
        public User register(User user) {
            user.setCreatedAt(LocalDateTime.now());
            user.setActive(false);
            var newUser = userRepository.save(user);
            logger.info("New user created: {}", newUser);
            if (newUser != null) {
                return newUser;
            } else {
                return null;
            }
        }

    public User login(User user) {
        Optional<User> findUser = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (findUser.isPresent()) {
            logger.info("User logged in: {}", findUser.get());
            UserLogin userLogin = new UserLogin();
            userLogin.setUsername(findUser.get().getUsername());
            userLogin.setLoginTime(LocalDateTime.now());
            userLoginRepository.save(userLogin);
            return findUser.get();
        } else {
            logger.error("Invalid username and password");
            return null;
        }
    }

    // forgot password

        // reset password
}
