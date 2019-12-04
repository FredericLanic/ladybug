package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

public class BodyImg {
  // Image de Ladybug
  private Image   img  = null;
  private BodyImg next = null;

  public BodyImg(Image img) {
    this.img = img;
  }

  public Image getImg() {
    return img;
  }

  public BodyImg getNext() {
    return next;
  }

  public void setNext(BodyImg next) {
    this.next = next;
  }
}
