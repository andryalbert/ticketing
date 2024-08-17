package com.demo.ticketing.service.Impl;

import com.demo.ticketing.dto.PisteAuditDto;
import com.demo.ticketing.model.PisteAudit;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.PisteAuditRepository;
import com.demo.ticketing.service.PisteAuditService;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PisteAuditServiceImpl implements PisteAuditService {

    private final PisteAuditRepository pisteAuditRepository;
    private final UserService userService;
    private final TicketService ticketService;

    @Override
    public void savePisteAudit(PisteAudit pisteAudit) {
        log.info("piste audit {}", pisteAudit);
        pisteAuditRepository.save(pisteAudit);
    }

    @Override
    public PisteAudit mapToEntity(PisteAuditDto dto) {
        log.info("pisteAudit dto dans mapper {}",dto);
        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId(dto.getUserId());
        pisteAudit.setDeleted(false);
        pisteAudit.setAction(dto.getAction());
        pisteAudit.setUserConcerned(dto.getUserConcerned());

        // check if user exist
        Optional<User> userOptional = userService.getUserByIdForPisteAudit(dto.getUserId());
        log.info("user dans mapper piste audit {}", userOptional);
        userOptional.ifPresent(pisteAudit::setUser);

        // check if ticket exist
        Optional<Ticket> ticketOptional = ticketService.getTicketByIdForPisteAudit(dto.getTicketId());
        log.info("ticket dans mapper piste audit {}", ticketOptional);
        ticketOptional.ifPresent(pisteAudit::setTicket);

        if (userOptional.isPresent() || ticketOptional.isPresent()) {
            log.info("la convertion du piste audit vers dto est effectué");
            return pisteAudit;
        } else {
            log.error("la piste audit dto est invalide {}", dto);
            throw new IllegalArgumentException("la piste audit dto est invalide");
        }
    }

    @Override
    public PisteAuditDto mapToDto(PisteAudit entity) {
        log.info("piste audit {}", entity);
        return PisteAuditDto.builder()
                .pisteAuditId(entity.getId())
                .userConcerned(entity.getUserConcerned())
                .action(entity.getAction())
                .lastUpdate(entity.getLastUpdate())
                .dateAction(LocalDate.from(entity.getLastUpdate()))
                .timeAction(LocalTime.from(entity.getLastUpdate()))
                .userId(entity.getUser().getId())
                .ticketId(entity.getTicket().getId())
                .build();
    }


}
