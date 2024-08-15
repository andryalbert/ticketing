package com.demo.ticketing.utils.mapper;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TicketMapper {

    private final UserService userService;

    public Ticket convertTicketDtoToTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setId(ticketDto.getTicketId());
        ticket.setDeleted(ticketDto.isDeleted());
        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setTicketStatus(ticketDto.getTicketStatus());

        // check if user exist
        Optional<User> userOptional = userService.getUserById(ticketDto.getUserId());
        log.info("user dans mapper ticket {}", userOptional);
        userOptional.ifPresent(ticket::setUser);

        if (userOptional.isPresent()) {
            log.info("la convertion du ticket vers dto est effectué");
            return ticket;
        } else {
            log.error("le ticket dto est invalide {}", ticketDto);
            throw new IllegalArgumentException("le ticket dto est invalide");
        }

    }

    public TicketDto convertTicketToTicketDto(Ticket ticket) {
        log.info("ticket {}", ticket);
        return TicketDto.builder()
                .ticketId(ticket.getId())
                .title(ticket.getTitle())
                .ticketStatus(ticket.getTicketStatus())
                .description(ticket.getDescription())
                .deleted(ticket.isDeleted())
                .userId(ticket.getUser().getId())
                .lastUpdate(ticket.getLastUpdate())
                .build();
    }

}
