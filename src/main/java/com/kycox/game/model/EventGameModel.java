package com.kycox.game.model;

import org.springframework.context.ApplicationEvent;

public class EventGameModel extends ApplicationEvent {
    private final GameModel gameModel;

    public EventGameModel(Object source, GameModel gameModel) {
        super(source);
        this.gameModel = gameModel;
    }

    public GameModel getGameModel() {
        return gameModel;
    }
}
