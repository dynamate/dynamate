package library.guarding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

public class ReceiverGuardBuilder implements GuardBuilder {

	private static final Lookup LOOKUP = MethodHandles.lookup();

	@Override
	public <K, PK> K buildCompositeKey(GuardStrategy<PK> guardStrategy, MethodType type, int receiverIndex, Object[] arguments) {
		return (K) guardStrategy.buildPartialKey(type, receiverIndex, arguments[receiverIndex]);
	}

	@Override
	public <K, PK> MethodHandle buildGuard(GuardStrategy<PK> guardStrategy, MethodType type, int receiverIndex, K compositeKey) throws NoSuchMethodException,
			IllegalAccessException {
		MethodHandle handle = MethodHandles.insertArguments(LOOKUP.findVirtual(GuardStrategy.class, "test", MethodType.methodType(boolean.class, Object.class, Object.class)), 0,
				guardStrategy, compositeKey);

		MethodHandle filter = LOOKUP.findVirtual(GuardStrategy.class, "buildPartialKey", MethodType.methodType(Object.class, MethodType.class, int.class, Object.class));
		filter = MethodHandles.insertArguments(filter, 0, guardStrategy, type);

		handle = MethodHandles.filterArguments(handle, 0, MethodHandles.insertArguments(filter, 0, receiverIndex));

		handle = MethodHandles.permuteArguments(handle, MethodType.genericMethodType(type.parameterCount()).changeReturnType(boolean.class), receiverIndex);
		handle = handle.asType(MethodType.methodType(boolean.class, type));

		return handle;
	}

}
