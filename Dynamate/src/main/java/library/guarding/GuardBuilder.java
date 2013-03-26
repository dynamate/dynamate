package library.guarding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public interface GuardBuilder {
	<K, PK> K buildCompositeKey(GuardStrategy<PK> guardStrategy, MethodType type, int receiverIndex, Object[] arguments);

	<K, PK> MethodHandle buildGuard(GuardStrategy<PK> guardStrategy, MethodType type, int receiverIndex, K compositeKey) throws NoSuchMethodException, IllegalAccessException;
}
