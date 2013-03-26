package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TimingUtils {

	private static final TimingUtils INSTANCE = new TimingUtils();

	private Map<String, AtomicLong[]> timers = new HashMap<>();

	public void startTimer(String timerId) {
		long t0 = System.nanoTime();

		AtomicLong[] timer = getTimer(timerId, true);
		timer[0] = new AtomicLong(t0);
	}

	public void stopTimer(String timerId) {
		long t1 = System.nanoTime();

		AtomicLong[] timer = getTimer(timerId, false);

		if (timer == null || timer[0] == null) {
			throw new IllegalStateException("Timer " + timerId + " not started.");
		}

		long t0 = timer[0].get();

		timer[1].getAndAdd(t1 - t0);
		timer[0] = null;
	}

	private AtomicLong[] getTimer(String timerId, boolean createIfNull) {
		AtomicLong[] pair = timers.get(timerId);

		if (pair == null && createIfNull) {
			pair = new AtomicLong[] { null, new AtomicLong() };
			timers.put(timerId, pair);
		}

		return pair;
	}

	public long getTimeInMillis(String timerId) {
		return TimeUnit.NANOSECONDS.toMillis(getTimeInNanos(timerId));
	}

	public long getTimeInNanos(String timerId) {
		AtomicLong[] timer = getTimer(timerId, false);

		if (timer == null) {
			throw new IllegalStateException("Timer " + timerId + " not created");
		}

		return timer[1].get();
	}

	public static TimingUtils getInstance() {
		return INSTANCE;
	}

	public Map<String, Long> getTimers() {
		Map<String, Long> timers = new HashMap<>();

		for (Entry<String, AtomicLong[]> entry : this.timers.entrySet()) {
			timers.put(entry.getKey(), entry.getValue()[1].get());
		}

		return timers;
	}
}
