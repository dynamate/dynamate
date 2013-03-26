package library.impl.cop;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import library.BoxedCallSite;
import library.DefaultMethodDispatcher;
import library.MethodDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	private static MethodDispatcher<Class<?>, Object> layeredDispatcher;

	static {
		layeredDispatcher = new DefaultMethodDispatcher<Class<?>, Object, Method>();
		layeredDispatcher.setMethodSelectionStrategy(new LayeredDispatchStrategy());
	}

	public static BoxedCallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return layeredDispatcher.dispatch(name, type);
	}
}
