package com.demo.ticketing.dto;

import com.demo.ticketing.model.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PisteAuditDto {
    private String pisteAuditId;
    private String userConcerned;
    private LocalDate dateAction;
    private LocalTime timeAction;
    private LocalDateTime lastUpdate;
    private Action action;
    private String userId;
    private String ticketId;
}
