package com.demo.ticketing.restController;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets(){

        return new ResponseEntity<>(new ArrayList<Ticket>(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id){

        return new ResponseEntity<>(new Ticket(),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketDto ticketDto){

        return new ResponseEntity<>(new Ticket(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@RequestBody TicketDto ticketDto, @PathVariable String id){

        return new ResponseEntity<>(new Ticket(), HttpStatus.OK);
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<Ticket> assignTicketToUser(@PathVariable String id,@PathVariable String userId){

        return new ResponseEntity<>(new Ticket(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable String id){

        return new ResponseEntity<>("le ticket est supprimé",HttpStatus.OK);
    }


}
