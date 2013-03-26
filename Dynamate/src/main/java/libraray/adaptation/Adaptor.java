package libraray.adaptation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Adaptor {
	private static final Logger logger = LoggerFactory.getLogger(Adaptor.class);

	private BasicArgumentTypeConverter argumentTypeConverter;

	private ArgumentSelector argumentSelector;

	private ArgumentInjector argumentInjector;

	public Adaptor(ArgumentSelector argumentSelector, BasicArgumentTypeConverter argumentTypeConverter, ArgumentInjector argumentInjector) {
		this.argumentSelector = argumentSelector;
		this.argumentTypeConverter = argumentTypeConverter;
		this.argumentInjector = argumentInjector;
	}

	public MethodHandle adaptMethodHandleToCallSite(MethodHandle targetHandle, MethodType callType) throws InstantiationException, IllegalAccessException, NoSuchMethodException {
		Class<?>[] methodFormalArguments = targetHandle.type().parameterArray();
		Class<?>[] callSiteFormalArguments = callType.parameterArray();

		if (methodFormalArguments[methodFormalArguments.length - 1].isArray() && !callSiteFormalArguments[callSiteFormalArguments.length - 1].isArray()) {
			targetHandle = targetHandle.asCollector(methodFormalArguments[methodFormalArguments.length - 1], (callSiteFormalArguments.length - methodFormalArguments.length) + 1);
			methodFormalArguments = targetHandle.type().parameterArray();
		}

		int i = 0;
		int j = 0;

		if (targetHandle.type().returnType().equals(void.class)) {
			targetHandle = targetHandle.asType(targetHandle.type().changeReturnType(callType.returnType()));
		}

		while (i < methodFormalArguments.length) {
			if (j < callSiteFormalArguments.length) {

				if (this.isConvertable(methodFormalArguments[i], callSiteFormalArguments[j])) {
					MethodHandle filter = MethodHandles.lookup().findVirtual(BasicArgumentTypeConverter.class, "convertType",
							MethodType.methodType(Object.class, Class.class, Class.class, Object.class));
					filter = MethodHandles.insertArguments(filter, 0, this.argumentTypeConverter, methodFormalArguments[i], callSiteFormalArguments[j]);
					filter = filter.asType(MethodType.methodType(methodFormalArguments[i], callSiteFormalArguments[j]));
					targetHandle = MethodHandles.filterArguments(targetHandle, j, filter);

					i++;
					j++;
				} else if (asObjectClass(methodFormalArguments[i]).isAssignableFrom(asObjectClass(callSiteFormalArguments[j]))
						|| asObjectClass(callSiteFormalArguments[j]).isAssignableFrom(asObjectClass(methodFormalArguments[i]))) {
					i++;
					j++;
				} else {
					j++;
				}
			} else {
				targetHandle = this.injectOptionalValues(targetHandle, methodFormalArguments, i);
				break;
			}
		}

		if (!this.checkTypes(targetHandle.type(), callType)) {
			return null;
		}

		return targetHandle;
	}

	public MethodHandle adaptOnDroppableArguments(MethodHandle handle) {
		int[] droppableArgumentsIndices = this.argumentSelector.absoluteDroppableArgumentsIndices();

		if (droppableArgumentsIndices != null) {
			for (int index : droppableArgumentsIndices) {
				handle = MethodHandles.dropArguments(handle, index, Object.class);
			}
		}

		return handle;
	}

	private boolean isConvertable(Class<?> methodFormalArgument, Class<?> callSiteFormalArgument) {
		return (this.argumentTypeConverter != null) && this.argumentTypeConverter.isConvertable(methodFormalArgument, callSiteFormalArgument);
	}

	private boolean checkTypes(MethodType methodType, MethodType callType) {

		Class<?>[] methodTypes = methodType.parameterArray();
		Class<?>[] callTypes = callType.parameterArray();

		if (methodTypes.length != callTypes.length) {
			Adaptor.logger.trace("length missmatch:" + Arrays.toString(methodTypes) + ", " + Arrays.toString(callTypes));
			return false;
		}

		for (int i = 0; i < methodTypes.length; i++) {
			if (!asObjectClass(callTypes[i]).isAssignableFrom(asObjectClass(methodTypes[i])) && !asObjectClass(methodTypes[i]).isAssignableFrom(asObjectClass(callTypes[i]))) {
				return false;
			}
		}

		return true;
	}

	private MethodHandle injectOptionalValues(MethodHandle targetHandle, Class<?>[] parameterArray, int startIndex) {
		if (this.argumentInjector != null) {
			for (int i = startIndex; i < parameterArray.length; i++) {
				Object value = this.argumentInjector.injectValue(parameterArray[i]);

				if (value == Exception.class) {
					return targetHandle;
				}

				targetHandle = MethodHandles.insertArguments(targetHandle, i, value);
			}
		}

		return targetHandle;
	}

	private static Class<?> asObjectClass(Class<?> clazz) {
		if (clazz.equals(byte.class)) {
			return Byte.class;
		}
		if (clazz.equals(short.class)) {
			return Short.class;
		}
		if (clazz.equals(int.class)) {
			return Integer.class;
		}
		if (clazz.equals(long.class)) {
			return Long.class;
		}
		if (clazz.equals(char.class)) {
			return Character.class;
		}
		if (clazz.equals(float.class)) {
			return Float.class;
		}
		if (clazz.equals(double.class)) {
			return Double.class;
		}
		if (clazz.equals(boolean.class)) {
			return Boolean.class;
		}
		if (clazz.equals(void.class)) {
			return Void.class;
		}

		return clazz;
	}
}
