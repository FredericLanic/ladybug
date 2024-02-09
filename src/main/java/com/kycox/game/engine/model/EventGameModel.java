package com.kycox.game.engine.model;

import org.springframework.context.ApplicationEvent;

public class EventGameModel extends ApplicationEvent {

    public EventGameModel(Object source) {
        super(source);
    }
}
