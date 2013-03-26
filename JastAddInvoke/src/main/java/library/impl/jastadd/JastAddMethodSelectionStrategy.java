package library.impl.jastadd;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.GuardStrategy;
import AST.ASTNode;

public class JastAddMethodSelectionStrategy implements MethodSelectionStrategy<Class<?>, ASTNode, Method> {

	private JastAddAstTraversing objectGraphIterator;

	private GuardStrategy<String> guardStrategy = new GuardStrategy<String>() {

		@Override
		public boolean test(String savedNodePath, String argumentNodePath) {
			return savedNodePath.equals(argumentNodePath);
		}

		@Override
		public String buildPartialKey(MethodType type, int index, Object argument) {
			if (index == 0) {
				ASTNode<?> node = (ASTNode<?>) argument;
				return node.astPredecessors.replaceAll("@\\d+", "");
			}

			return null;
		}

		@Override
		public library.guarding.GuardStrategy.GuardType getGuardType() {
			return GuardType.RECEIVER_GUARD;
		}
	};

	public JastAddMethodSelectionStrategy(JastAddAstTraversing objectGraphIterator) {
		this.objectGraphIterator = objectGraphIterator;
	}

	@Override
	public SimplifiedMethodHandle<Method> selectMethod(final ObjectGraphIteratingHistory<Class<?>, ASTNode> history, MethodType callType, Class<?> clazz, String methodName,
			Object[] arguments) {
		if (history.size() == 0) {
			return null;
		}

		MethodType receiverlessType = callType.dropParameterTypes(0, 1);

		Method method = null;

		try {
			method = clazz.getMethod(methodName, receiverlessType.parameterArray());
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}

		SimplifiedMethodHandle<Method> handle = new SimplifiedMethodHandle<Method>(clazz, methodName, false, method.getReturnType(), method.getParameterTypes());

		handle.setArgumentFilter(new JastAddArgumentFilter(this.objectGraphIterator, history));
		handle.setGuardStrategy(this.guardStrategy);

		return handle;
	}

	@Override
	public SimplifiedMethodHandle<Method> adapt(Class<?> receiverType, MethodType callType, SimplifiedMethodHandle<Method> methodHandle) {
		// TODO Auto-generated method stub
		return null;
	}

}
