package com.demo.ticketing.utils;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Ticket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketMapper {

    public static Ticket convertTicketDtoToTicket(TicketDto ticketDto){

        return new Ticket();
    }

    public static TicketDto convertTicketToTicketDto(Ticket ticket){

        return new TicketDto();
    }

}
