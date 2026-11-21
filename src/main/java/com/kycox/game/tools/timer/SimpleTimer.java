package com.kycox.game.tools.timer;

import com.kycox.game.constant.TimerStatus;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class SimpleTimer {
	/** Visible du package pour les tests (interruption, S2142). */
	static class WaiterTimer extends TimerTask {
		private static final Logger logger = LoggerFactory.getLogger(WaiterTimer.class);
		private final long duration;

		@Getter
		private final Timer timer;

		@Setter
		private TimerStatus timerStatus;

		WaiterTimer(long time) {
			timer = new Timer(true);
			timerStatus = TimerStatus.RUN;
			duration = time;
		}

		private void forcedStop() {
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
		}

		boolean isRunning() {
			return timerStatus == TimerStatus.RUN;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(duration);
			} catch (InterruptedException interruptedException) {
				logger.error(String.valueOf(interruptedException));
				// rend le signal d'interruption à qui l'attend (règle Java S2142)
				Thread.currentThread().interrupt();
			} finally {
				timer.cancel();
				timer.purge();
				timerStatus = TimerStatus.STOP;
			}
		}
	}

	private WaiterTimer waitTimer;

	public void forcedStop() {
		if (waitTimer != null) {
			waitTimer.forcedStop();
		}
	}

	public boolean isRunning() {
		return (waitTimer != null) && waitTimer.isRunning();
	}

	public void launch(long millisecondsDuration) {
		waitTimer = new WaiterTimer(millisecondsDuration);
		waitTimer.setTimerStatus(TimerStatus.RUN);
		waitTimer.getTimer().schedule(waitTimer, millisecondsDuration);
	}
}
