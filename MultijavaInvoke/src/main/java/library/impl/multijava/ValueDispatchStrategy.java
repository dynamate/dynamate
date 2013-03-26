package library.impl.multijava;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.GuardStrategy;
import multijava.valuedispatch.OnValue;
import multijava.valuedispatch.ValueDispatchable;

public class ValueDispatchStrategy implements MethodSelectionStrategy<Class<?>, Object, Method> {

	private GuardStrategy<Object> guardStrategy = new GuardStrategy<Object>() {

		@Override
		public Object buildPartialKey(MethodType type, int index, Object argument) {
			// use the concrete value as an partial key
			return argument;
		}

		@Override
		public boolean test(Object savedPartialKey, Object argumentPartialKey) {
			return savedPartialKey.equals(argumentPartialKey);
		}

		@Override
		public library.guarding.GuardStrategy.GuardType getGuardType() {
			return GuardType.ARGUMENTS_GUARD;
		}
	};

	@Override
	public SimplifiedMethodHandle<Method> selectMethod(ObjectGraphIteratingHistory<Class<?>, Object> resolveHistory, MethodType callType, Class<?> runtimeClass, String method,
			Object[] arguments) {

		MethodType receiverlessType = callType.dropParameterTypes(0, 1);

		try {
			// lookup the default method
			Method defaultMethod = runtimeClass.getMethod(method, receiverlessType.parameterArray());
			Method target = defaultMethod;

			// try to fetch the ValueDispatchable annotation to find sub methods
			ValueDispatchable annotation = defaultMethod.getAnnotation(ValueDispatchable.class);

			if (annotation != null) {
				List<Integer> indicesToProtect = new ArrayList<Integer>();

				// loop through all sub methods denoted by the ValueDispatchable annotation
				for (int methodIndex : annotation.value()) {
					boolean foundMethod = true;
					indicesToProtect.clear();

					// lookup the sub method
					Method subMethod = runtimeClass.getMethod(method + "$" + methodIndex, receiverlessType.parameterArray());

					Annotation[][] parameterAnnotations = subMethod.getParameterAnnotations();
					// loop through each parameter's annotation
					for (int i = 1; i < arguments.length; i++) {
						// get the value of the OnValue annotation if set, or null if parameter is not annotaed with it
						Object value = this.getDispatchValue(parameterAnnotations[i - 1]);

						if (value != null) {
							indicesToProtect.add(i);

							// check whether the concrete argument matches the annotated value, if not, this sub method must not be considered
							if (!value.equals(arguments[i].toString())) {
								foundMethod = false;
								break;
							}
						}
					}

					// if a suitable sub method is found, set it as the target method
					if (foundMethod) {
						target = subMethod;
						break;
					}
				}

				SimplifiedMethodHandle<Method> handle = new SimplifiedMethodHandle<Method>(target.getDeclaringClass(), target.getName(), false, target.getReturnType(),
						target.getParameterTypes());

				// setup a guard stratgy for the indices that need to be protected, i.e., the ones with an OnValue annotation
				handle.setGuardStrategy(this.guardStrategy);

				return handle;
			} else {
				// method is not value dispatchable, use always the default one (no guard needed in this case)
				return new SimplifiedMethodHandle<Method>(target.getDeclaringClass(), target.getName(), false, target.getReturnType(), target.getParameterTypes());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// get the value of the OnValue annotation from a set of parameter annotations
	private Object getDispatchValue(Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (a.annotationType().equals(OnValue.class)) {
				return ((OnValue) a).value();
			}
		}

		return null;
	}

	@Override
	public SimplifiedMethodHandle<Method> adapt(Class<?> receiverType, MethodType callType, SimplifiedMethodHandle<Method> methodHandle) {
		// TODO Auto-generated method stub
		return null;
	}
}