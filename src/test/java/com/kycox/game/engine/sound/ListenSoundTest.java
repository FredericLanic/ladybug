package com.kycox.game.engine.sound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.sound.sampled.Clip;

import org.junit.jupiter.api.Test;

/**
 * Un son joué sur un fil virtuel (règle S6881) : le clip démarre, la tâche attend
 * sa fin, puis le rembobine pour la prochaine fois. Faux clip : pas besoin de
 * carte son.
 */
class ListenSoundTest {

	@Test
	void aClipIsPlayedOnAVirtualThreadThenRewound() throws InterruptedException {
		var clip = mock(Clip.class);
		when(clip.getMicrosecondLength()).thenReturn(1_000L);
		when(clip.getMicrosecondPosition()).thenReturn(0L, 500L, 1_000L);

		var thread = ListenSound.play(clip);
		thread.join(2_000);

		assertThat(thread.isVirtual()).isTrue();
		assertThat(thread.isAlive()).isFalse();
		verify(clip).start();
		verify(clip).setFramePosition(0);
	}

	@Test
	void aClipAlreadyPlayingIsLeftAlone() throws InterruptedException {
		var clip = mock(Clip.class);
		when(clip.isRunning()).thenReturn(true);

		ListenSound.play(clip).join(2_000);

		verify(clip, org.mockito.Mockito.never()).start();
	}
}
