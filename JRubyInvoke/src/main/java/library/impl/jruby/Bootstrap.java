package library.impl.jruby;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import libraray.adaptation.BasicArgumentInjector;
import libraray.adaptation.BasicArgumentTypeConverter;
import library.DefaultMethodDispatcher;
import library.MethodDispatcher;

import org.jruby.RubyArray;
import org.jruby.RubyClass;
import org.jruby.runtime.Block;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.invokedynamic.InvocationLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

	private final static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	private static MethodDispatcher<RubyClass, IRubyObject> methodDispatcher;

	private static boolean skipIdLibrary = Boolean.getBoolean("skipIdLibrary");

	static {
		BasicArgumentInjector argumentInjector = new BasicArgumentInjector();
		argumentInjector.registerConstantInjection(Block.class, Block.NULL_BLOCK);

		BasicArgumentTypeConverter argumentTypeConverter = new BasicArgumentTypeConverter();
		argumentTypeConverter.registerTypeConversion(IRubyObject[].class, RubyArray.class, new RubyArrayToHostArrayConverter());
		argumentTypeConverter.registerTypeConversion(IRubyObject[].class, IRubyObject.class, new SingletonArrayConverter());

		methodDispatcher = new DefaultMethodDispatcher<>();
		methodDispatcher.setArgumentSelector(new JRubyArgumentSelector());
		methodDispatcher.setMethodSelectionStrategy(new JRubyMethodSelectionStrategy());
		methodDispatcher.setObjectGraphIterator(new JRubyHierarchyResolver());
		methodDispatcher.setTypeResolver(new JRubyTypeResolver());
		methodDispatcher.setArgumentInjector(argumentInjector);
		methodDispatcher.setArgumentTypeConverter(argumentTypeConverter);

		JRubyExceptionHandler jRubyExceptionHandler = new JRubyExceptionHandler();
		methodDispatcher.getExceptionHandlers().put("callIter", jRubyExceptionHandler);
		methodDispatcher.getExceptionHandlers().put("fcallIter", jRubyExceptionHandler);
		methodDispatcher.getExceptionHandlers().put("vcallIter", jRubyExceptionHandler);
	}

	public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		if (skipIdLibrary) {
			return InvocationLinker.invocationBootstrap(lookup, name, type);
		}

		// logger.info("Bootstrapping name: {}, type: {}", name, type);
		return methodDispatcher.dispatch(lookup, name, type);
	}
}
