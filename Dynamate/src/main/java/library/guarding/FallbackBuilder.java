package library.guarding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import library.BoxedCallSite;

public interface FallbackBuilder {
	<K, V, PK> MethodHandle buildFallback(BoxedCallSite<K, V> callSite, MethodHandle initialFallbackHandle, CachingStrategy<K, V> cachingStrategy, GuardStrategy<PK> guardStrategy,
			GuardBuilder guardBuilder, MethodType type, int receiverIndex) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException;

}
