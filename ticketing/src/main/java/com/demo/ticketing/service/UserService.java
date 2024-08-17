package com.demo.ticketing.service;

import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends  MapperService<User, UserDto>{
    Optional<User> getUserById(String id);

    User getUserByUserName(String username);

    Optional<User> getUserByIdForPisteAudit(String id);

    User saveUser(User user);

    List<User> getAllUsers();

}
