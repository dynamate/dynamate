package library.guarding;

import java.lang.invoke.MethodHandle;

import library.BoxedCallSite;

public class DefaultCachingStrategy<K> implements CachingStrategy<K, MethodHandle> {

	@Override
	public void cache(BoxedCallSite<K, MethodHandle> callSite, K key, MethodHandle handle, Object[] args) {
		callSite.putCachedValue(key, handle);
	}

	@Override
	public MethodHandle retrieve(BoxedCallSite<K, MethodHandle> callSite, MethodHandle fallbackHandle, K key) {
		MethodHandle methodHandle = callSite.getCachedValue(key);

		if (methodHandle != null) {
			return methodHandle;
		}

		return fallbackHandle;
	}
}
