package library.impl.multijava;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.GuardStrategy;

public class MultiDispatchStrategy implements MethodSelectionStrategy<Class<?>, Object, Method> {

	private GuardStrategy<Class<?>> guardStrategy = new GuardStrategy<Class<?>>() {
		@Override
		public Class<?> buildPartialKey(MethodType type, int index, Object argument) {
			return argument.getClass();
		}

		@Override
		public boolean test(Class<?> savedArgumentClass, Class<?> argumentClass) {
			return savedArgumentClass == argumentClass;
		}

		@Override
		public library.guarding.GuardStrategy.GuardType getGuardType() {
			return GuardType.ARGUMENTS_GUARD;
		}
	};

	@Override
	public SimplifiedMethodHandle<Method> selectMethod(ObjectGraphIteratingHistory<Class<?>, Object> resolveHistory, MethodType callType, Class<?> receiverType, String method,
			Object[] arguments) {

		// determine runtime types of arguments
		List<Class<?>> argumentTypes = new ArrayList<>(arguments.length + 1);
		for (Object arg : arguments) {
			argumentTypes.add(arg.getClass());
		}

		// find methods in the receiver's class hierarchy
		Method[] declaredMethods = receiverType.getMethods();

		Map<List<Class<?>>, Method> applicableMethods = new LinkedHashMap<>();

		// find all applicable methods, i.e., all methods stating the same name and argument count
		for (Method m : declaredMethods) {
			if (m.getName().equals(method)) {
				List<Class<?>> parameterTypes = new ArrayList<Class<?>>(Arrays.asList(m.getParameterTypes()));
				parameterTypes.add(0, m.getDeclaringClass());

				// check if the method's parameter types are compatible to the call type
				if (this.isApplicable(parameterTypes, argumentTypes)) {
					applicableMethods.put(parameterTypes, m);
				}
			}
		}

		if (applicableMethods.isEmpty()) {
			throw new RuntimeException();
		}

		Entry<List<Class<?>>, Method> mostApplicableEntry = null;

		// out of all applicable methods, determine the most applicable one, i.e., the one with the tightest of runtime type matching
		for (Entry<List<Class<?>>, Method> entry : applicableMethods.entrySet()) {
			if ((mostApplicableEntry == null) || this.isApplicable(mostApplicableEntry.getKey(), entry.getKey())) {
				mostApplicableEntry = entry;
			}
		}

		// setup the simplified method handle pointing to the most applicable method
		Class<?> implementationClass = mostApplicableEntry.getValue().getDeclaringClass();
		String methodName = mostApplicableEntry.getValue().getName();
		boolean isStatic = false;
		Class<?> returnType = mostApplicableEntry.getValue().getReturnType();
		List<Class<?>> parameterTypes = mostApplicableEntry.getKey().subList(1, mostApplicableEntry.getKey().size());

		SimplifiedMethodHandle<Method> handle = new SimplifiedMethodHandle<Method>(implementationClass, methodName, isStatic, returnType, parameterTypes);

		// attacha guard to protect the target method
		handle.setGuardStrategy(this.guardStrategy);
		return handle;
	}

	private boolean isApplicable(List<Class<?>> typesA, List<Class<?>> typesB) {

		if (typesA.size() != typesB.size()) {
			return false;
		}

		for (int i = 0; i < typesA.size(); i++) {
			if (!typesA.get(i).isAssignableFrom(typesB.get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public SimplifiedMethodHandle<Method> adapt(Class<?> receiverType, MethodType callType, SimplifiedMethodHandle<Method> methodHandle) {
		// TODO Auto-generated method stub
		return null;
	}
}
