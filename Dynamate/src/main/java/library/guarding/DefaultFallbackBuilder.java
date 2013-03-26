package library.guarding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import library.BoxedCallSite;

public class DefaultFallbackBuilder implements FallbackBuilder {

	@Override
	public <K, V, PK> MethodHandle buildFallback(BoxedCallSite<K, V> callSite, MethodHandle initialFallbackHandle, CachingStrategy<K, V> cachingStrategy,
			GuardStrategy<PK> guardStrategy, GuardBuilder guardBuilder, MethodType type, int receiverIndex) throws NoSuchMethodException, IllegalAccessException,
			ClassNotFoundException {

		MethodHandle key = MethodHandles.lookup().findVirtual(GuardBuilder.class, "buildCompositeKey",
				MethodType.methodType(Object.class, GuardStrategy.class, MethodType.class, int.class, Object[].class));
		key = MethodHandles.insertArguments(key, 0, guardBuilder, guardStrategy, type, receiverIndex);
		key = key.asCollector(Object[].class, type.parameterCount());
		key = key.asType(type.changeReturnType(Object.class));

		MethodHandle cachedHandleResolver = MethodHandles.insertArguments(
				MethodHandles.lookup().findVirtual(CachingStrategy.class, "retrieve", MethodType.methodType(Object.class, BoxedCallSite.class, Object.class, Object.class)), 0,
				cachingStrategy, callSite, initialFallbackHandle);

		cachedHandleResolver = MethodHandles.dropArguments(cachedHandleResolver, 1, type.parameterArray());
		cachedHandleResolver = MethodHandles.foldArguments(cachedHandleResolver, key);
		cachedHandleResolver = cachedHandleResolver.asType(cachedHandleResolver.type().changeReturnType(MethodHandle.class));

		return MethodHandles.foldArguments(MethodHandles.invoker(type), cachedHandleResolver);
	}
}
