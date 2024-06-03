package org.example.controller;

import org.example.model.User;
import org.example.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("user")
    public List<User> getUsers() {
        return userService.fetchUserList();
    }

    @PostMapping("user")
    public void addUser(@RequestBody User userData) {
        userService.saveUser(userData);
    }

    @PostMapping("user/delete")
    public void deleteUser(@RequestBody Long userId) {
        userService.deleteUserById(userId);
    }
}
