package com.demo.ticketing.model;

public enum Action {
    CREATE("créé"),
    UPDATE("modifié"),
    ASSIGN("assigné"),
    DELETE("supprimé");

    private final String action;

    Action(String action) {
        this.action = action;
    }

}
