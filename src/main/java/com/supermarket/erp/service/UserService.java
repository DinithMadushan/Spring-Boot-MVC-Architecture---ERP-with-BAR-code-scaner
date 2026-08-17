package com.supermarket.erp.service;

import com.supermarket.erp.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getByUsername(String username);

    /**
     * Saves a new user, hashing the raw password. If editing an existing user
     * and rawPassword is blank, the existing hash is preserved.
     */
    User saveUser(User user, String rawPassword);

    void deleteUser(Long id);

}
