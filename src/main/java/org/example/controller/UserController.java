package org.example.controller;

import org.example.model.User;
import org.example.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("user")
    public void addUser(@RequestBody User userData) {
        userService.saveUser(userData);
    }

    @PostMapping("user/delete")
    public void deleteUser(@RequestBody Long userId) {
        userService.deleteUserById(userId);
    }
}
