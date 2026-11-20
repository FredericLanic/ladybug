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
package com.kycox.game.engine.score;

import com.kycox.game.tools.Position;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class GroupMessages {
	private final List<Message> messages = new ArrayList<>();

	public void add(Position position, String value, MessageType messageType) {
		messages.add(new Message(position, value, messageType));
	}

	/** Avance tous les messages d'un battement (appelé par la boucle du jeu, 3.5.1d). */
	public void tick() {
		messages.forEach(Message::tick);
	}

	public void removeIfDying() {
		messages.removeIf(Message::isDying);
	}
}
