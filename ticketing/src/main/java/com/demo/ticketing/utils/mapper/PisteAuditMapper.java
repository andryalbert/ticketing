package com.demo.ticketing.utils.mapper;

import com.demo.ticketing.dto.PisteAuditDto;
import com.demo.ticketing.model.PisteAudit;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class PisteAuditMapper {

    private final UserService userService;
    private final TicketService ticketService;

    public PisteAudit convertPisteAuditDtoToPisteAudit(PisteAuditDto pisteAuditDto) {
        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId(pisteAuditDto.getUserId());
        pisteAudit.setDeleted(false);
        pisteAudit.setAction(pisteAuditDto.getAction());
        pisteAudit.setUserConcerned(pisteAuditDto.getUserConcerned());

        // check if user exist
        Optional<User> userOptional = userService.getUserById(pisteAuditDto.getUserId());
        log.info("user dans mapper piste audit {}", userOptional);
        userOptional.ifPresent(pisteAudit::setUser);

        // check if ticket exist
        Optional<Ticket> ticketOptional = ticketService.getTicketById(pisteAuditDto.getTicketId());
        log.info("ticket dans mapper piste audit {}", ticketOptional);
        ticketOptional.ifPresent(pisteAudit::setTicket);

        if (userOptional.isPresent() || ticketOptional.isPresent()) {
            log.info("la convertion du piste audit vers dto est effectué");
            return pisteAudit;
        } else {
            log.error("la piste audit dto est invalide {}", pisteAuditDto);
            throw new IllegalArgumentException("la piste audit dto est invalide");
        }

    }

    public PisteAuditDto convertPisteAuditToPisteAuditDto(PisteAudit pisteAudit) {
        log.info("piste audit {}", pisteAudit);
        return PisteAuditDto.builder()
                .pisteAuditId(pisteAudit.getId())
                .userConcerned(pisteAudit.getUserConcerned())
                .action(pisteAudit.getAction())
                .lastUpdate(pisteAudit.getLastUpdate())
                .dateAction(LocalDate.from(pisteAudit.getLastUpdate()))
                .timeAction(LocalTime.from(pisteAudit.getLastUpdate()))
                .userId(pisteAudit.getUser().getId())
                .ticketId(pisteAudit.getTicket().getId())
                .build();
    }

}
