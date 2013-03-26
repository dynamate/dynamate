package library.impl.multijava;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import library.BoxedCallSite;
import library.DefaultMethodDispatcher;
import library.MethodDispatcher;

public class Bootstrap {

	private static MethodDispatcher<Class<?>, Object> multiDispatcher, valueDispatcher;

	static {
		multiDispatcher = new DefaultMethodDispatcher<Class<?>, Object, Method>();
		multiDispatcher.setMethodSelectionStrategy(new MultiDispatchStrategy());

		valueDispatcher = new DefaultMethodDispatcher<Class<?>, Object, Method>();
		valueDispatcher.setMethodSelectionStrategy(new ValueDispatchStrategy());
	}

	public static BoxedCallSite bootstrapMultiDispatch(Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return multiDispatcher.dispatch(lookup, name, type);
	}

	public static BoxedCallSite bootstrapValueDispatch(Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return valueDispatcher.dispatch(lookup, name, type);
	}
}
