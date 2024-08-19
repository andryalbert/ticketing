package com.demo.ticketing.restController;

import com.demo.ticketing.dto.TicketDto;
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
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController extends AbstractController {

    private final TicketService ticketService;
    private final UserService userService;
    private final PisteAuditService pisteAuditService;

    @GetMapping
    @Operation(
            tags = {"Ticketing"},
            summary = "Récupérer tous les tickets",
            description = "Récupérer tous les tickets en se basant au utilisateur connecté sans besoin des variables d'entrées et attend une liste de ticket en retour",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TicketDto.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        // get all ticket depending on the user who connect
        User user = getCurrentUserInDatabase(currentUser());
        List<TicketDto> ticketDtos = ticketService.getAllTicketsByUser(user).stream().map(ticketService::mapToDto).toList();
        return new ResponseEntity<>(ticketDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            tags = {"Ticketing"},
            summary = "Récupérer un ticket par son ID",
            description = "Récupérer un ticket par son ID en se basant au utilisateur connecté avec une variable d'entrée et un ticket en retour",
            parameters = {@Parameter(name = "id",description = "ticket id",example = "6018d139-bd41-40d5-855e-da4eca517556",in = ParameterIn.PATH)},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TicketDto.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<TicketDto> getTicketById(@PathVariable String id) {
        // get all ticket depending on the user who connect and the id giving
        User user = getCurrentUserInDatabase(currentUser());
        TicketDto ticketDto = ticketService.mapToDto(ticketService.getTicketByUserAndId(user, id).orElse(new Ticket()));
        return new ResponseEntity<>(ticketDto, HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            tags = {"Ticketing"},
            summary = "Créer un nouveau ticket",
            description = "Créer un nouveau ticket en se basant au utilisateur connecté avec un ticket dto en entrée et un ticket en retour",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "variable d'entrée avec un tickect dto",
                    content = @Content(schema = @Schema(implementation = TicketDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = TicketDto.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Bonne reponse"
                    )
            },
            security = {@SecurityRequirement(name = "BasicAuth")}
    )
    public ResponseEntity<TicketDto> createTicket(@RequestBody TicketDto ticketDto) {
        // the ticket is linked directly on the user who create
        User user = getCurrentUserInDatabase(currentUser());
        ticketDto.setUserId(user.getId());
        Ticket ticket = ticketService.mapToEntity(ticketDto);
        Ticket ticket1 = ticketService.saveTicket(ticket);
        TicketDto ticketDto1 = ticketService.mapToDto(ticket1);
        // save Piste audit
        PisteAudit pisteAudit = getPisteAudit(Action.CREATE, ticket1);
        pisteAuditService.savePisteAudit(pisteAudit);
        return new ResponseEntity<>(ticketDto1, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            tags = {"Ticketing"},
            summary = "Mettre à jour un ticket existant",
            description = "Mettre à jour un ticket existant en se basant au utilisateur connecté avec un ticket dto et un variable sur le lien en entrée et un ticket en retour",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "variable d'entrée avec un tickect dto",
                    content = @Content(schema = @Schema(implementation = TicketDto.class))
            ),
            parameters = {@Parameter(name = "id",description = "ticket id",example = "6018d139-bd41-40d5-855e-da4eca517556",in = ParameterIn.PATH)},
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
    public ResponseEntity<TicketDto> updateTicket(@RequestBody TicketDto ticketDto, @PathVariable String id) {
        // check if this ticket is exist for the user
        Optional<Ticket> ticket = ticketService.getTicketByUserAndId(getCurrentUserInDatabase(currentUser()), id);
        if (ticket.isPresent()) {
            ticketDto.setTicketId(id);
            ticketDto.setUserId(currentUser().getId());
            Ticket ticket1 = ticketService.saveTicket(ticketService.mapToEntity(ticketDto));
            TicketDto ticketDto1 = ticketService.mapToDto(ticket1);
            // save Piste audit
            PisteAudit pisteAudit = getPisteAudit(Action.UPDATE, ticket1);
            pisteAuditService.savePisteAudit(pisteAudit);
            return new ResponseEntity<>(ticketDto1, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("l'user ne peut pas modifier un ticket avec id " + id + " qui ne lui appartient pas");
        }
    }

    @PutMapping("/{id}/assign/{userId}")
    @Operation(
            tags = {"Ticketing"},
            summary = "Assigner un ticket à un utilisateur",
            description = "Assigner un ticket à un utilisateur en se basant au utilisateur connecté avec deux variables sur le lien en entrée et un ticket en retour",
            parameters = {
                    @Parameter(name = "id",description = "ticket id",example = "6018d139-bd41-40d5-855e-da4eca517556",in = ParameterIn.PATH),
                    @Parameter(name = "userId",description = "user id",example = "6018d139-bd41-40d5-855e-da4eca517556",in = ParameterIn.PATH)
            },
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
    public ResponseEntity<TicketDto> assignTicketToUser(@PathVariable String id, @PathVariable String userId) {
        // check if this ticket is exist for the user
        Optional<Ticket> ticketOptional = ticketService.getTicketByUserAndId(getCurrentUserInDatabase(currentUser()), id);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            // check if the user to affect the task is existed
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                ticket.setUser(user.get());
                Ticket ticket1 = ticketService.saveTicket(ticket);
                TicketDto ticketDto = ticketService.mapToDto(ticket1);
                // save Piste audit
                PisteAudit pisteAudit = getPisteAudit(Action.ASSIGN, ticket1);
                pisteAuditService.savePisteAudit(pisteAudit);
                return new ResponseEntity<>(ticketDto, HttpStatus.OK);
            } else {
                throw new IllegalArgumentException("l'user id: " + userId + " n'existe pas");
            }
        } else {
            throw new IllegalArgumentException("l'user ne peut pas modifier un ticket avec id " + id + " qui ne lui appartient pas");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            tags = {"Ticketing"},
            summary = "Supprimer un ticket par son ID",
            description = "Supprimer un ticket par son ID avec un variables sur le lien en entrée et un ticket en retour",
            parameters = {
                    @Parameter(name = "id",description = "ticket id",example = "6018d139-bd41-40d5-855e-da4eca517556",in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
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
    public ResponseEntity<String> deleteTicket(@PathVariable String id) {
        // check if this ticket is exist for the user
        Optional<Ticket> ticketOptional = ticketService.getTicketByUserAndId(getCurrentUserInDatabase(currentUser()), id);
        if (ticketOptional.isPresent()) {
            ticketService.deleteTicket(ticketOptional.get());
            // save Piste audit
            PisteAudit pisteAudit = getPisteAudit(Action.DELETE, ticketOptional.get());
            pisteAuditService.savePisteAudit(pisteAudit);
            return new ResponseEntity<>("le ticket avec id: "+id+" est supprimé", HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("le ticket avec id: " + id + " n'existe pas");
        }
    }

    public User getCurrentUserInDatabase(User user){
        return userService.getUserByUserName(user.getUsername());
    }

}
