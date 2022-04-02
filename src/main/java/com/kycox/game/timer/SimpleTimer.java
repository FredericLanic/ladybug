package com.kycox.game.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.TimerStatus;

import lombok.Getter;
import lombok.Setter;

public class SimpleTimer {
	private class WaiterTimer extends TimerTask {
		private long		duration;
		@Getter
		private Timer		timer;
		@Setter
		private TimerStatus	timerStatus;

		private WaiterTimer(long time) {
			timer		= new Timer(true);
			timerStatus	= TimerStatus.RUN;
			duration	= time;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(duration);
			} catch (InterruptedException interruptedException) {
				logger.error(interruptedException);
			} finally {
				timer.cancel();
				timer.purge();
				timerStatus = TimerStatus.STOP;
			}
		}

		private void forcedStop() {
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
		}

		private boolean isRunning() {
			return timerStatus == TimerStatus.RUN;
		}
	}

	private static final Log logger	= LogFactory.getLog(WaiterTimer.class);
	private WaiterTimer		 waitTimer;

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
