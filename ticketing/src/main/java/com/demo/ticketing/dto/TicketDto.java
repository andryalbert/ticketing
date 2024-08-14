package com.demo.ticketing.dto;

import com.demo.ticketing.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {
    private String ticketId;
    private LocalDateTime lastUpdate;
    private boolean deleted;
    private String title;
    private String description;
    private String userId;
    private TicketStatus ticketStatus;
}
