package com.demo.ticketing.utils.mapper;

import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserMapper {

    public User convertUserDtoToUser(UserDto userDto) {
        log.info("user dto {}", userDto);
        User user = new User();
        user.setId(userDto.getUserId());
        user.setDeleted(userDto.isDeleted());
        user.setUserName(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return user;
    }

    public UserDto convertUserToUserDto(User user) {
        log.info("user {}", user);
        return UserDto.builder()
                .userId(user.getId())
                .lastUpdate(user.getLastUpdate())
                .deleted(user.isDeleted())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

}
