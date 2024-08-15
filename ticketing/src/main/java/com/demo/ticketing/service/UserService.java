package com.demo.ticketing.service;

import com.demo.ticketing.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(String id);

    User saveUser(User user);

    List<User> getAllUsers();

}
