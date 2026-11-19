package com.kycox.game.engine.sound;

import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kycox.game.constant.sounds.Sounds;

/**
 * Durée des sons, lue dans l'en-tête des fichiers {@code .wav} (étape 3.5.3b).
 *
 * <p>Certaines attentes du jeu durent exactement le temps d'un son : la musique
 * du début de niveau, l'intermède entre deux niveaux, l'agonie de Ladybug. Le
 * modèle a donc besoin de ces durées, mais pas d'une carte son : on les lit dans
 * le fichier, sans ouvrir de sortie audio. Ainsi le jeu, les tests et un poste
 * sans son ont les mêmes durées.
 */
@Component
public class SoundDurations {
	private static final Logger logger = LoggerFactory.getLogger(SoundDurations.class);

	/**
	 * Durée du son en millisecondes, arrondie comme
	 * {@code Clip.getMicrosecondLength() / 1000} ; 0 si le fichier est illisible.
	 */
	public long of(Sounds sound) {
		var url = SoundDurations.class.getClassLoader().getResource("sound/wav/" + sound.getFileName());
		try {
			var format = AudioSystem.getAudioFileFormat(url);
			var microseconds = (long) (format.getFrameLength() * 1_000_000L / format.getFormat().getFrameRate());
			return microseconds / 1000;
		} catch (Exception e) {
			logger.warn("Cannot read the duration of sound {}: {}", sound, e.toString());
			return 0;
		}
	}
}
