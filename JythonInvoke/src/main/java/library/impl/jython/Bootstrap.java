package library.impl.jython;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

import library.DefaultMethodDispatcher;
import library.MethodDispatcher;

import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyType;
import org.python.core.opt.SpecializeCallSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

	static MethodDispatcher<PyType, PyObject> dispatcher;

	private final static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	private static boolean skipIdLibrary = Boolean.getBoolean("skipIdLibrary");

	static {
		dispatcher = new DefaultMethodDispatcher<PyType, PyObject, PyMethod>();
		dispatcher.setMethodSelectionStrategy(new JythonMethodSelectionStrategy());
		dispatcher.setObjectGraphIterator(new JythonHierarchyResolver());
		dispatcher.setTypeResolver(new JythonTypeResolver());
		dispatcher.setArgumentSelector(new JythonArgumentSelector());
	}

	public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		if (skipIdLibrary) {
			return SpecializeCallSite.bootstrap(lookup, name, type);
		}

		return dispatcher.dispatch(lookup, name, type);
	}

	public static CallSite bootstrap(Lookup lookup, String name, MethodType type, Object keyword) {
		if (skipIdLibrary) {
			return SpecializeCallSite.bootstrap(lookup, name, type, keyword);
		}

		return null;
	}

	public static CallSite bootstrap(Lookup lookup, String name, MethodType type, Object[] kws) {
		if (skipIdLibrary) {
			return SpecializeCallSite.bootstrap(lookup, name, type, kws);
		}

		return null;
	}
}
