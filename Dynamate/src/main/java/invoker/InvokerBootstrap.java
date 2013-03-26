package invoker;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

import library.BoxedCallSite;
import library.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokerBootstrap {
	private static MethodHandle initialResolveHandle;

	private final static Logger logger = LoggerFactory.getLogger(InvokerBootstrap.class);

	public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		logger.trace("Bootstrapping name: {}, type: {}", name, type);

		BoxedCallSite<Pair<String, MethodType>, MethodHandle> callSite = new BoxedCallSite<>(type, 2);

		MethodHandle initialResolveHandle = MethodHandles.lookup().findStatic(InvokerBootstrap.class, "resolve",
				MethodType.methodType(Object.class, BoxedCallSite.class, Class.class, String.class, String.class, MethodType.class, Object[].class));
		initialResolveHandle = MethodHandles.insertArguments(initialResolveHandle, 0, callSite);
		callSite.setTarget(initialResolveHandle);

		InvokerBootstrap.initialResolveHandle = initialResolveHandle;

		return callSite;
	}

	private static Object resolve(BoxedCallSite<Pair<String, MethodType>, MethodHandle> realCallSite, Class<?> bootstrapClass, String bootstrapMethod, String identifier,
			MethodType type, Object... arguments) {
		logger.trace("Resolving method by forwarding name: {}, type: {} to class: {}, method: {}", new Object[] { identifier, type, bootstrapClass, bootstrapMethod });

		Lookup lookup = MethodHandles.lookup();
		Object result = null;

		try {
			MethodHandle bootstrapHandle = lookup.findStatic(bootstrapClass, bootstrapMethod,
					MethodType.methodType(BoxedCallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class));

			BoxedCallSite<?, ?> logicalCallSite = (BoxedCallSite<?, ?>) bootstrapHandle.invokeWithArguments(lookup, identifier, type);

			MethodHandle handle = logicalCallSite.dynamicInvoker();

			MethodHandle initialHandle = InvokerBootstrap.initialResolveHandle;
			MethodHandle fallbackHandle = fallbackHandle(initialHandle, realCallSite);

			MethodHandle guardHandle = guardHandle(realCallSite, null, initialHandle, identifier, type);

			MethodHandle adaptedHandle = MethodHandles.dropArguments(handle, 0, Class.class, String.class, String.class, MethodType.class);
			adaptedHandle = adaptedHandle.asSpreader(Object[].class, type.parameterCount());
			adaptedHandle = adaptedHandle.asType(adaptedHandle.type().changeReturnType(Object.class));

			Pair<String, MethodType> pair = new Pair<String, MethodType>(identifier, type);

			realCallSite.setTarget(realCallSite.guard(guardHandle, adaptedHandle, fallbackHandle));

			realCallSite.putCachedValue(pair, adaptedHandle);

			result = handle.invokeWithArguments(arguments);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return result;
	}

	private static MethodHandle fallbackHandle(MethodHandle initialHandle, BoxedCallSite<Pair<String, MethodType>, MethodHandle> callSite) {
		MethodHandle handle = null;

		try {
			handle = MethodHandles.lookup().findStatic(InvokerBootstrap.class, "fallback",
					MethodType.methodType(Object.class, MethodHandle.class, BoxedCallSite.class, Class.class, String.class, String.class, MethodType.class, Object[].class));
			handle = MethodHandles.insertArguments(handle, 0, initialHandle, callSite);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return handle;
	}

	private static Object fallback(MethodHandle initialHandle, BoxedCallSite<Pair<String, MethodType>, MethodHandle> callSite, Class<?> bootstrapClass, String bootstrapMethod,
			String identifier, MethodType type, Object[] arguments) throws Throwable {
		MethodHandle handle;

		Pair<String, MethodType> pair = new Pair<String, MethodType>(identifier, type);
		boolean isCached = callSite.hasCachedValue(pair);

		if (isCached) {
			logger.trace("Fallbacking to cached method handle for {}", pair);
			handle = callSite.getCachedValue(pair);
		} else {
			logger.trace("Fallbacking to initial resolving for {}", pair);
			handle = initialHandle;
		}

		return handle.invokeWithArguments(bootstrapClass, bootstrapMethod, identifier, type, arguments);
	}

	private static MethodHandle guardHandle(BoxedCallSite<Pair<String, MethodType>, MethodHandle> callSite, CallSite dynamicFallbackCallSite, MethodHandle initialHandle,
			String savedIdentifier, MethodType savedType) {
		MethodHandle handle = null;

		try {
			handle = MethodHandles.lookup().findStatic(
					InvokerBootstrap.class,
					"guard",
					MethodType.methodType(boolean.class, BoxedCallSite.class, CallSite.class, MethodHandle.class, String.class, MethodType.class, String.class, MethodType.class,
							Object[].class));
			handle = MethodHandles.insertArguments(handle, 0, callSite, dynamicFallbackCallSite, initialHandle, savedIdentifier, savedType);
			handle = MethodHandles.dropArguments(handle, 0, Class.class, String.class);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return handle;
	}

	private static boolean guard(BoxedCallSite<Pair<String, MethodType>, MethodHandle> callSite, CallSite dynamicFallbackCallSite, MethodHandle initialHandle,
			String savedIdentifier, MethodType savedType, String identifier, MethodType type, Object[] arguments) {
		return savedIdentifier.equals(identifier) && savedType.equals(type);
	}
}
