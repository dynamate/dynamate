package library.guarding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

public class ArgumentGuardBuilder implements GuardBuilder {

	private static final Lookup LOOKUP = MethodHandles.lookup();

	@Override
	public <K, PK> K buildCompositeKey(GuardStrategy<PK> guardStrategy, MethodType type, int receiverIndex, Object[] arguments) {

		List<PK> compositeKey = new ArrayList<PK>();

		for (int i = 0; i < arguments.length; i++) {
			PK partialKey = guardStrategy.buildPartialKey(type, i, arguments[i]);
			compositeKey.add(partialKey);
		}

		return (K) compositeKey;
	}

	@Override
	public <K, PK> MethodHandle buildGuard(GuardStrategy<PK> guardStrategy, MethodType type, int receiverIndex, K compositeKey) throws NoSuchMethodException,
			IllegalAccessException {
		Class[] parameterArray = type.parameterArray();

		MethodHandle constantTrue = MethodHandles.dropArguments(MethodHandles.constant(boolean.class, true), 0, parameterArray);
		MethodHandle constantFalse = MethodHandles.dropArguments(MethodHandles.constant(boolean.class, false), 0, parameterArray);
		MethodHandle lastHandle = constantTrue;

		List<PK> key = (List<PK>) compositeKey;

		MethodHandle filter = LOOKUP.findVirtual(GuardStrategy.class, "buildPartialKey", MethodType.methodType(Object.class, MethodType.class, int.class, Object.class));
		filter = MethodHandles.insertArguments(filter, 0, guardStrategy, type);

		for (int i = key.size() - 1; i >= 0; i--) {
			Object saved = key.get(i);

			if (saved != null) {
				MethodHandle handle;

				handle = MethodHandles.insertArguments(
						MethodHandles.lookup().findVirtual(GuardStrategy.class, "test", MethodType.methodType(boolean.class, Object.class, Object.class)), 0, guardStrategy, saved);

				handle = MethodHandles.filterArguments(handle, 0, MethodHandles.insertArguments(filter, 0, i));
				handle = handle.asType(MethodType.methodType(boolean.class, type.parameterType(i)));

				Class[] drops = new Class[i];
				for (int j = 0; j < drops.length; j++) {
					drops[j] = parameterArray[j];
				}

				handle = MethodHandles.dropArguments(handle, 0, drops);
				handle = MethodHandles.guardWithTest(handle, lastHandle, constantFalse);
				lastHandle = handle;
			}
		}

		return lastHandle;
	}
}
