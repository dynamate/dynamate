package library.impl.jastadd;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import library.BoxedCallSite;
import library.DefaultMethodDispatcher;
import library.MethodDispatcher;
import AST.ASTNode;

public class Bootstrap {
	private static MethodDispatcher<Class<?>, ASTNode> methodDispatcher;

	static {
		JastAddAstTraversing objectGraphIterator = new JastAddAstTraversing();

		methodDispatcher = new DefaultMethodDispatcher<Class<?>, ASTNode, Method>();
		methodDispatcher.setObjectGraphIterator(objectGraphIterator);
		methodDispatcher.setMethodSelectionStrategy(new JastAddMethodSelectionStrategy(objectGraphIterator));
	}

	public static BoxedCallSite bootstrap(Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return methodDispatcher.dispatch(name, type);
	}
}
