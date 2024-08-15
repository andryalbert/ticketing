package com.demo.ticketing.restController;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Action;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.service.PisteAuditService;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.service.UserService;
import com.demo.ticketing.utils.mapper.PisteAuditMapper;
import com.demo.ticketing.utils.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController extends AbstractController{

    private final TicketService ticketService;
    private final UserService userService;
    private final PisteAuditService pisteAuditService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets(){
        // get all ticket depending on the user who connect
        return new ResponseEntity<>(ticketService.getAllTicketsByUser(currentUser()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id){
        // get all ticket depending on the user who connect and the id giving
        return new ResponseEntity<>(ticketService.getTicketByUserById(currentUser(),id).orElse(new Ticket()),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketDto ticketDto){
        // the ticket is linked directly on the user who create
        ticketDto.setUserId(currentUser().getId());
        Ticket ticket = ticketService.saveTicket(new TicketMapper(userService).convertTicketDtoToTicket(ticketDto));
        // save Piste audit
        pisteAuditService.savePisteAudit(
                new PisteAuditMapper(userService,ticketService)
                        .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.CREATE, new Ticket(), ticket.getId())));
        return new ResponseEntity<>(new Ticket(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@RequestBody TicketDto ticketDto, @PathVariable String id){
        // check if this ticket is exist for the user
        Optional<Ticket> ticket = ticketService.getTicketByUserById(currentUser(),id);
        if(ticket.isPresent()){
            ticketDto.setTicketId(id);
            ticketDto.setUserId(currentUser().getId());
            Ticket ticket1 = ticketService.saveTicket(new TicketMapper(userService).convertTicketDtoToTicket(ticketDto));
            // save Piste audit
            pisteAuditService.savePisteAudit(
                    new PisteAuditMapper(userService,ticketService)
                            .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.UPDATE, new Ticket(), ticket1.getId())));
            return new ResponseEntity<>(ticket1, HttpStatus.OK);
        }else{
            throw new IllegalArgumentException("l'user ne peut pas modifier un ticket avec id "+id+" qui ne lui appartient pas");
        }
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<Ticket> assignTicketToUser(@PathVariable String id,@PathVariable String userId){
        // check if this ticket is exist for the user
        Optional<Ticket> ticketOptional = ticketService.getTicketByUserById(currentUser(),id);
        if(ticketOptional.isPresent()){
            Ticket ticket = ticketOptional.get();
            // check if the user to affect the task is existed
            Optional<User> user = userService.getUserById(userId);
            if(user.isPresent()){
                ticket.setUser(user.get());
                Ticket ticket1 = ticketService.saveTicket(ticket);
                // save Piste audit
                pisteAuditService.savePisteAudit(
                        new PisteAuditMapper(userService,ticketService)
                                .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.ASSIGN, new Ticket(), ticket1.getId())));
                return new ResponseEntity<>(ticket1, HttpStatus.OK);
            }else{
                throw new IllegalArgumentException("l'user id: "+userId+" n'existe pas");
            }
        }else{
            throw new IllegalArgumentException("l'user ne peut pas modifier un ticket avec id "+id+" qui ne lui appartient pas");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable String id){
        // check if this ticket is exist for the user
        Optional<Ticket> ticketOptional = ticketService.getTicketByUserById(currentUser(),id);
        if(ticketOptional.isPresent()){
            ticketService.deleteTicket(ticketOptional.get());
            // save Piste audit
            pisteAuditService.savePisteAudit(
                    new PisteAuditMapper(userService,ticketService)
                            .convertPisteAuditDtoToPisteAudit(getPisteAuditDto(Action.DELETE, new Ticket(), ticketOptional.get().getId())));
        }else{
            throw new IllegalArgumentException("le ticket id: "+id+" n'existe pas");
        }
        return new ResponseEntity<>("le ticket est supprimé",HttpStatus.OK);
    }


}
