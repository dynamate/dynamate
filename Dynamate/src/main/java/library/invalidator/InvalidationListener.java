package library.invalidator;

import java.lang.invoke.SwitchPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidationListener {

	private final Logger logger = LoggerFactory.getLogger(InvalidationListener.class);
	private SwitchPoint[] switchPoint;

	boolean invalidated = false;

	public InvalidationListener(SwitchPoint switchPoint) {
		this.switchPoint = new SwitchPoint[] { switchPoint };
	}

	public void invalidate() {
		if (!this.invalidated) {
			SwitchPoint.invalidateAll(this.switchPoint);
		}
	}
}
