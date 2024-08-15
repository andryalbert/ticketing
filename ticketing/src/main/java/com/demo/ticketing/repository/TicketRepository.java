package com.demo.ticketing.repository;

import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findByUserAndDeleted(User user, boolean deleted);

    Optional<Ticket> findByUserAndIdAndDeleted(User user, String id, boolean deleted);

    Optional<Ticket> findByIdAndDeleted(String id, boolean deleted);
}
