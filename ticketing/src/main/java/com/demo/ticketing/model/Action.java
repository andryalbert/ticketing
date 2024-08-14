package com.demo.ticketing.model;

public enum Action {
    CREATE("créé"),
    MODIFY("modifié"),
    DELETE("supprimé");

    private final String action;

    Action(String action) {
        this.action = action;
    }

}
