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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends AbstractController {

    private final UserService userService;
    private final TicketService ticketService;
    private final PisteAuditService pisteAuditService;

    @GetMapping
    @Operation(
            tags = {"Ticketing"},
            summary = "Récupérer tous les utilisateurs",
            description = "Récupérer tous les utilisateurs sans besoin des variables d'entrées et attend une liste d'user en retour",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}/ticket")
    @Operation(
            tags = {"Ticketing"},
            summary = "Récupérer les tickets assignés à l'utilisateur",
            description = "Récupérer les tickets assignés à l'utilisateur avec une variable d'entrée et une liste ticket en retour",
            parameters = {@Parameter(name = "id",description = "user id",example = "6018d139-bd41-40d5-855e-da4eca517556",in = ParameterIn.PATH)},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = Ticket.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Mauvaise reponse"
                    )

            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<List<Ticket>> getAllTicketsForUser(@PathVariable String id) {
        // checked if id is valid
        Optional<User> user = userService.getUserById(id);
        List<Ticket> tickets;
        if (user.isPresent()) {
            tickets = ticketService.getAllTicketsByUser(user.get());
        } else {
            throw new IllegalArgumentException("l'user avec l'id: " + id + " n'existe pas");
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            tags = {"Ticketing"},
            summary = "Créer un utilisateur",
            description = "Créer un utilisateur avec un user dto en entrée et un user en retour",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "variable d'entrée avec un user dto",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        User user = userService.saveUser(new UserMapper().convertUserDtoToUser(userDto));
        // save audit
        pisteAuditService.savePisteAudit(
                new PisteAuditMapper(userService, ticketService)
                        .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.CREATE, new User(), user.getId())));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            tags = {"Ticketing"},
            summary = "Modifier un utilisateur",
            description = "Modifier un utilisateur avec un user dto et un variable sur le lien en entrée et un user en retour",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "variable d'entrée avec un user dto",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            parameters = {@Parameter(name = "id",description = "user id",example = "6018d139-bd41-40d5-855e-da4eca517556",in = ParameterIn.PATH)},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Mauvaise reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<User> updateUser(@RequestBody UserDto userDto, @PathVariable String id) {
        // checked if id is valid
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userDto.setUserId(id);
            User user1 = userService.saveUser(new UserMapper().convertUserDtoToUser(userDto));
            // save audit
            pisteAuditService.savePisteAudit(
                    new PisteAuditMapper(userService, ticketService)
                            .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.UPDATE, new User(), user1.getId())));
            return new ResponseEntity<>(user1, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("l'user avec l'id: " + id + " n'existe pas");
        }
    }


}
