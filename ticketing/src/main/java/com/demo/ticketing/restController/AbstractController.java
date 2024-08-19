package com.demo.ticketing.restController;

import com.demo.ticketing.model.Action;
import com.demo.ticketing.model.PisteAudit;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@Slf4j
public abstract class AbstractController {

    protected User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication {}", authentication.getPrincipal());
        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = new User();
            user.setUserName(userDetails.getUsername());
            return user;
        }
        return null;
    }

    protected PisteAudit getPisteAudit(Action action, Object object) {
        log.info("action {}", action);
        log.info("object {}", object);
        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId(IdGenerator.uuid());
        pisteAudit.setAction(action);
        pisteAudit.setUserConcerned(currentUser().getUsername());
        pisteAudit.setDeleted(false);
        pisteAudit.setLastUpdate(LocalDateTime.now());
        pisteAudit.setTicket((object instanceof Ticket) ? (Ticket)object : null);
        pisteAudit.setUser((object instanceof User) ? (User)object : null);
        return pisteAudit;
    }


}
