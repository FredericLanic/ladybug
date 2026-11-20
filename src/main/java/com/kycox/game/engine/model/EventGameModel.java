package com.kycox.game.engine.model;

import com.kycox.game.snapshot.GameSnapshot;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Publié à la fin de chaque battement. Depuis 4.6, il transporte la photo de la
 * partie ({@link GameSnapshot}) : les vues et les sons ne lisent qu'elle. La
 * source reste le modèle, pour les contrôleurs qui agissent sur le jeu (en
 * attendant {@code PlayerInput}, étape 5).
 */
public class EventGameModel extends ApplicationEvent {

	/** La photo de la partie à la fin de ce battement. */
	@Getter
	private final transient GameSnapshot snapshot;

	public EventGameModel(Object source, GameSnapshot snapshot) {
		super(source);
		this.snapshot = snapshot;
	}
}
