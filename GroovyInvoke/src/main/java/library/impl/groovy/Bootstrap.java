package library.impl.groovy;

import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import libraray.adaptation.BasicArgumentTypeConverter;
import library.DefaultMethodDispatcher;
import library.MethodDispatcher;

import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.vmplugin.v7.IndyInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

	private static MethodDispatcher<MetaClass, Object> dispatcher;

	private static boolean skipIdLibrary = Boolean.getBoolean("skipIdLibrary");

	private final static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	static {
		BasicArgumentTypeConverter converter = new BasicArgumentTypeConverter();
		converter.registerTypeConversion(Object.class, Wrapper.class, new WrapperConverter());

		dispatcher = new DefaultMethodDispatcher<MetaClass, Object, MetaMethod>();
		dispatcher.setArgumentTypeConverter(converter);
		dispatcher.setTypeResolver(new GroovyTypeResolver());
		dispatcher.setMethodSelectionStrategy(new GroovyMethodSelectionStrategy());
	}

	public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		if (skipIdLibrary) {
			return IndyInterface.bootstrap(caller, name, type);
		}

		return dispatcher.dispatch(name, type);
	}

	public static CallSite bootstrapCurrent(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		if (skipIdLibrary) {
			return IndyInterface.bootstrapCurrent(caller, name, type);
		}

		return dispatcher.dispatch(name, type);
	}

	public static CallSite bootstrapSafe(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		if (skipIdLibrary) {
			return IndyInterface.bootstrapSafe(caller, name, type);
		}

		return null;
	}

	public static CallSite bootstrapCurrentSafe(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		if (skipIdLibrary) {
			return IndyInterface.bootstrapCurrentSafe(caller, name, type);
		}

		return null;
	}
}
