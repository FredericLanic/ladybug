package com.kycox.ladybug.engine.model;

import com.kycox.ladybug.constant.GameStatusEnum;

public class GameStatus {
  private GameStatusEnum gameStatusEnum = GameStatusEnum.NO_GAME;
  private int            numLevel;

  GameStatus() {
  }

  public void addNumLevel() {
    numLevel++;
  }

  public int getNumLevel() {
    return numLevel;
  }

  public boolean isToConfiguration() {
    return gameStatusEnum == GameStatusEnum.TO_CONF_LOCAL_USR;
  }

  public boolean isInGame() {
    return gameStatusEnum == GameStatusEnum.IN_GAME;
  }

  public void setToConfigutation() {
    gameStatusEnum = GameStatusEnum.TO_CONF_LOCAL_USR;
  }

  public void setInGame() {
    gameStatusEnum = GameStatusEnum.IN_GAME;
  }

  public void setNumLevel(int numLevel) {
    this.numLevel = numLevel;
  }

  public void setStopGame() {
    gameStatusEnum = GameStatusEnum.NO_GAME;
  }
}
