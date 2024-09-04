package com.myfinbank.controller;

import com.myfinbank.entity.User;
import com.myfinbank.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    // login
    @PostMapping("/login")
    public User login(@RequestBody User user) { //Param("username") String userName, @RequestParam("password") String password) {
            User user1 = authService.login(user);
        if (!ObjectUtils.isEmpty(user1)) {
            return user1;
        } else {
            return null;
        }
    }
    // logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam("username") String username) {
        boolean isLogout = authService.logout(username);
        if (isLogout) {
            return ResponseEntity.ok().body("User logged out successfully .......");
        } else {
            return ResponseEntity.badRequest().body("User logout failed .......");
        }
    }

    // register
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User regUser = authService.register(user);
        if (regUser != null)
            return ResponseEntity.ok().body(regUser); // "User created successfully";
        else
            return ResponseEntity.badRequest().body(null); //"User creation failed";
    }

    // forgot password

    // reset password

}
