package library.guarding;

import java.lang.invoke.MethodHandle;

import library.BoxedCallSite;

public interface CachingStrategy<K, V> {
	void cache(BoxedCallSite<K, V> callSite, K key, MethodHandle value, Object[] args);

	V retrieve(BoxedCallSite<K, V> callSite, V fallback, K key);
}
