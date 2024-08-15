package com.demo.ticketing.restController;

import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.Action;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.service.PisteAuditService;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.service.UserService;
import com.demo.ticketing.utils.mapper.PisteAuditMapper;
import com.demo.ticketing.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends AbstractController{

    private final UserService userService;
    private final TicketService ticketService;
    private final PisteAuditService pisteAuditService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}/ticket")
    public ResponseEntity<List<Ticket>> getAllTicketsForUser(@PathVariable String id) {
        // checked if id is valid
        Optional<User> user = userService.getUserById(id);
        List<Ticket> tickets;
        if (user.isPresent()) {
            tickets = ticketService.getAllTicketsByUser(user.get());
        }else {
            throw new IllegalArgumentException("l'user avec l'id: " + id + " n'existe pas");
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        User user = userService.saveUser(new UserMapper().convertUserDtoToUser(userDto));
        // save audit
        pisteAuditService.savePisteAudit(
                new PisteAuditMapper(userService,ticketService)
                        .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.CREATE, new User(), user.getId())));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody UserDto userDto, @PathVariable String id) {
        // checked if id is valid
        Optional<User> user = userService.getUserById(id);
        User user1;
        if (user.isPresent()) {
            userDto.setUserId(id);
            user1 = userService.saveUser(new UserMapper().convertUserDtoToUser(userDto));
            // save audit
            pisteAuditService.savePisteAudit(
                    new PisteAuditMapper(userService,ticketService)
                            .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.UPDATE, new User(), user1.getId())));
        } else {
            throw new IllegalArgumentException("l'user avec l'id: " + id + " n'existe pas");
        }
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }


}
