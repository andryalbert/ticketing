package com.demo.ticketing.restController;

import com.demo.ticketing.dto.PisteAuditDto;
import com.demo.ticketing.model.Action;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
public abstract class AbstractController {

    protected User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User)
            return (User) authentication.getPrincipal();
        return null;
    }

    protected PisteAuditDto getPisteAuditDto(Action action,Object object,String idObject){
        log.info("action {}",action);
        log.info("object {}",object);
        log.info("idObject {}",idObject);
        return PisteAuditDto.builder()
                .pisteAuditId(IdGenerator.uuid())
                .userConcerned(currentUser().getUsername())
                .action(action)
                .lastUpdate(LocalDateTime.now())
                .dateAction(LocalDate.now())
                .timeAction(LocalTime.now())
                .userId((object instanceof User) ? idObject : null)
                .ticketId((object instanceof Ticket) ? idObject : null)
                .build();
    }

}
