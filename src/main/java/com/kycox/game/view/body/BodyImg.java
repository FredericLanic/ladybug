/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game.view.body;

import java.awt.Image;

import lombok.Getter;

public class BodyImg {
	@Getter
	private Image image;
	@Getter
	private BodyImg next;

	public BodyImg(Image img) {
		this.image = img;
	}

	public void setNext(BodyImg next) {
		this.next = next;
	}
}
