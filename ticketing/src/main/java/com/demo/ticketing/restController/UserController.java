package com.demo.ticketing.restController;

import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){

        return new ResponseEntity<>(new ArrayList<User>(), HttpStatus.OK);
    }

    @GetMapping("/{id}/ticket")
    public ResponseEntity<List<Ticket>> getAllTicketsForUser(@PathVariable String id){

        return new ResponseEntity<>(new ArrayList<Ticket>(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto){

        return new ResponseEntity<>(new User(),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody UserDto userDto,@PathVariable String id){

        return new ResponseEntity<>(new User(),HttpStatus.OK);
    }


}
