package com.demo.ticketing.restController;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.Action;
import com.demo.ticketing.model.PisteAudit;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.service.PisteAuditService;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.service.UserService;
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
                                    schema = @Schema(implementation = UserDto.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtos = userService.getAllUsers().stream().map(userService::mapToDto).toList();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
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
                                    schema = @Schema(implementation = TicketDto.class),
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
    public ResponseEntity<List<TicketDto>> getAllTicketsForUser(@PathVariable String id) {
        // checked if id is valid
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            List<Ticket> tickets = ticketService.getAllTicketsByUser(user.get());
            List<TicketDto> ticketDtos = tickets.stream().map(ticketService::mapToDto).toList();
            return new ResponseEntity<>(ticketDtos, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("l'user avec l'id: " + id + " n'existe pas");
        }
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
                                    schema = @Schema(implementation = UserDto.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = userService.saveUser(userService.mapToEntity(userDto));
        UserDto userDto1 = userService.mapToDto(user);
        // save audit
        PisteAudit pisteAudit = getPisteAudit(Action.CREATE, user);
        pisteAuditService.savePisteAudit(pisteAudit);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
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
                                    schema = @Schema(implementation = UserDto.class),
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
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable String id) {
        // checked if id is valid
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userDto.setUserId(id);
            User user1 = userService.saveUser(userService.mapToEntity(userDto));
            UserDto userDto1 = userService.mapToDto(user1);
            // save audit
            PisteAudit pisteAudit = getPisteAudit(Action.UPDATE, user1);
            pisteAuditService.savePisteAudit(pisteAudit);
            return new ResponseEntity<>(userDto1, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("l'user avec l'id: " + id + " n'existe pas");
        }
    }


}
