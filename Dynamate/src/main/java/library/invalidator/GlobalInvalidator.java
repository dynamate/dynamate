package library.invalidator;

import java.util.HashMap;
import java.util.Map;

public class GlobalInvalidator {

	public static Map<Object, Invalidator> invalidators = new HashMap<>();

	public static class SimpleInvalidator implements Invalidator {

		private InvalidationListener invalidationListener;

		@Override
		public void setInvalidationListener(InvalidationListener invalidationListener) {
			this.invalidationListener = invalidationListener;
		}
	}

	public static void invalidate(Object key) {
//		Invalidator invalidator = invalidators.get(key);
//
//		if (invalidator != null) {
//
//			InvalidationListener invalidationListener = ((SimpleInvalidator) invalidator).invalidationListener;
//
//			if (invalidationListener != null) {
//				invalidationListener.invalidate();
//			}
//		}
	}

	public static synchronized Invalidator getInvalidator(Object key) {
		Invalidator invalidator = invalidators.get(key);

		if (invalidator == null) {
			invalidator = new SimpleInvalidator();
			invalidators.put(key, invalidator);
		}

		return invalidator;
	}
}
