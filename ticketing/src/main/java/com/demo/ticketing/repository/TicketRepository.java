package com.demo.ticketing.repository;

import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    List<Ticket> findByUser(User user);
}
