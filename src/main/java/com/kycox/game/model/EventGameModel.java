package com.kycox.game.model;

import org.springframework.context.ApplicationEvent;

public class EventGameModel extends ApplicationEvent {

    public EventGameModel(Object source) {
        super(source);
    }
}
