package com.demo.ticketing.utils;

import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserMapper {

    public static User convertUserDtoToUser(UserDto userDto){

        return new User();
    }

    public static UserDto convertUserToUserDto(User user){

        return new UserDto();
    }

}
