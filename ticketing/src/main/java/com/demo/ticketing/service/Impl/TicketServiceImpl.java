package com.demo.ticketing.service.Impl;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.TicketRepository;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.service.UserService;
import com.demo.ticketing.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    @Override
    public Optional<Ticket> getTicketById(String id) {
        log.info("ticket id {}", id);
        return ticketRepository.findByIdAndDeleted(id, false);
    }

    @Override
    public Optional<Ticket> getTicketByIdForPisteAudit(String id) {
        log.info("ticket id {}", id);
        return ticketRepository.findById(id);
    }

    @Override
    public List<Ticket> getAllTicketsByUser(User user) {
        log.info("user {}", user);
        return ticketRepository.findByUserAndDeleted(user, false);
    }

    @Override
    public Optional<Ticket> getTicketByUserById(User user, String id) {
        log.info("user {}", user);
        log.info("ticket id {}", id);
        return ticketRepository.findByUserAndIdAndDeleted(user, id, false);
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        if (ticket.getId() == null || ticket.getId().isEmpty()) {
            ticket.setId(IdGenerator.uuid());
            ticket.setDeleted(false);
        }
        log.info("ticket {}", ticket);
        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Ticket ticket) {
        log.info("ticket {}", ticket);
        // do a soft delete
        ticket.setDeleted(true);
        ticketRepository.save(ticket);
    }


    @Override
    public Ticket mapToEntity(TicketDto dto) {
        Ticket ticket = new Ticket();
        ticket.setId(dto.getTicketId());
        ticket.setDeleted(dto.isDeleted());
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setTicketStatus(dto.getTicketStatus());

        // check if user exist
        Optional<User> userOptional = userService.getUserById(dto.getUserId());
        log.info("user dans mapper ticket {}", userOptional);
        userOptional.ifPresent(ticket::setUser);

        if (userOptional.isPresent()) {
            log.info("la convertion du ticket vers dto est effectué");
            return ticket;
        } else {
            log.error("le ticket dto est invalide {}", dto);
            throw new IllegalArgumentException("le ticket dto est invalide");
        }
    }

    @Override
    public TicketDto mapToDto(Ticket entity) {
        log.info("ticket {}", entity);
        return TicketDto.builder()
                .ticketId(entity.getId())
                .title(entity.getTitle())
                .ticketStatus(entity.getTicketStatus())
                .description(entity.getDescription())
                .deleted(entity.isDeleted())
                .userId(entity.getUser().getId())
                .lastUpdate(entity.getLastUpdate())
                .build();
    }

}
