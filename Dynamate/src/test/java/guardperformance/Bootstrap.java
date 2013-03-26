package guardperformance;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import libraray.methodselection.MethodSelectionStrategy;
import library.BoxedCallSite;
import library.DefaultMethodDispatcher;
import library.MethodDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	private static MethodDispatcher<Class<?>, Object>[] dispatcher = new MethodDispatcher[12];

	static <T, O, M> void configure(boolean caching, int guards, MethodDispatcher<Class<?>, Object> dispatcher2,
			MethodSelectionStrategy<Class<?>, Object, Method> methodSelectionStrategy) {
		dispatcher2.setMethodSelectionStrategy(methodSelectionStrategy);
		dispatcher2.setCascadingGuards(guards);
		dispatcher2.setCachingEnabled(caching);
	}

	static {
		MethodSelectionStrategy<Class<?>, Object, Method> methodSelectionStrategy = new ExamplaryMethodSelectionStrategy();

		for (int i = 0; i < 12; i++) {
			dispatcher[i] = new DefaultMethodDispatcher<Class<?>, Object, Method>();
		}

		for (int i = 0; i < 6; i++) {
			configure(false, i, dispatcher[i], methodSelectionStrategy);
		}

		for (int i = 6; i < 12; i++) {
			configure(true, i - 6, dispatcher[i], methodSelectionStrategy);
		}
	}

	// no caching, no guard
	public static BoxedCallSite bootstrap0(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[0].dispatch(name, type);
	}

	// no caching, 1 guard
	public static BoxedCallSite bootstrap1(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[1].dispatch(name, type);
	}

	// no caching, 2 guards
	public static BoxedCallSite bootstrap2(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[2].dispatch(name, type);
	}

	// no caching, 3 guards
	public static BoxedCallSite bootstrap3(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[3].dispatch(name, type);
	}

	// no caching, 4 guards
	public static BoxedCallSite bootstrap4(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[4].dispatch(name, type);
	}

	// no caching, 5 guards
	public static BoxedCallSite bootstrap5(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[5].dispatch(name, type);
	}

	// caching, 0 guard
	public static BoxedCallSite bootstrap6(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[6].dispatch(name, type);
	}

	// caching, 1 guards
	public static BoxedCallSite bootstrap7(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[7].dispatch(name, type);
	}

	// caching, 2 guards
	public static BoxedCallSite bootstrap8(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[8].dispatch(name, type);
	}

	// caching, 3 guards
	public static BoxedCallSite bootstrap9(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[9].dispatch(name, type);
	}

	// caching, 4 guards
	public static BoxedCallSite bootstrap10(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[10].dispatch(name, type);
	}

	// caching, 5 guards
	public static BoxedCallSite bootstrap11(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return dispatcher[11].dispatch(name, type);
	}
}
