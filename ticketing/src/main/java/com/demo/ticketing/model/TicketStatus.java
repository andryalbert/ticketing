package com.demo.ticketing.model;

public enum TicketStatus {

    IN_PROGRESS("en cours"),
    FINISHED("terminé"),
    CANCELLED("annulé");


    private final String status;

    TicketStatus(String status) {
        this.status = status;
    }
}
