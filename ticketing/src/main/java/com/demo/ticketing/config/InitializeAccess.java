package com.demo.ticketing.config;

import com.demo.ticketing.model.User;
import com.demo.ticketing.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeAccess {

    private final UserService userService;

    @PostConstruct
    public void initData(){
        List<User> userList = userService.getAllUsers();
        if(userList.size() == 0) {
            User user = new User();
            user.setUserName("test");
            user.setEmail("test@gmail.com");
            user.setPassword("1234Test$");
            userService.saveUser(user);
        }
    }

}
