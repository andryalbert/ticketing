package com.demo.ticketing.utils;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Ticket;

public class TicketMapper {

    public static Ticket convertTicketDtoToTicket(TicketDto ticketDto){

        return new Ticket();
    }

    public static TicketDto convertTicketToTicketDto(Ticket ticket){

        return new TicketDto();
    }

}
