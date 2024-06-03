package org.example.service;

import org.example.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Save operation
    void saveUser(User user);

    // Read operation
    List<User> fetchUserList();

    // Find by id
    Optional<User> fetchById(Long id);

    // Update operation
    User updateUser(User user);

    // Delete operation
    void deleteUserById(Long userId);
}
