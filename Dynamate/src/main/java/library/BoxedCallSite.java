package library;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.SwitchPoint;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import library.invalidator.InvalidationListener;
import library.invalidator.Invalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxedCallSite<K, V> extends MutableCallSite implements Cloneable {

	private final static Logger logger = LoggerFactory.getLogger(BoxedCallSite.class);

	private final int maxNumberOfCallSites;

	private Deque<Pair<MethodHandle, MethodHandle>> lastHandles;

	public BoxedCallSite(MethodType type) {
		this(type, 1);
	}

	public BoxedCallSite(MethodType type, int maxNumberOfCallSites) {
		super(type);
		this.maxNumberOfCallSites = maxNumberOfCallSites;
		this.lastHandles = new ArrayDeque<>(maxNumberOfCallSites);
	}

	private ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

	static volatile SwitchPoint globalSwitchPoint = createNewGlobalSwitchPoint();

	public boolean hasCachedValue(K object) {
		return this.cache.containsKey(object);
	}

	public V getCachedValue(K key) {
		return this.cache.get(key);
	}

	public void putCachedValue(K key, V value) {
		this.cache.putIfAbsent(key, value);
	}

	public boolean hasCachedValues() {
		return !this.cache.isEmpty();
	}

	public int getNumberOfCachedValues() {
		return this.cache.size();
	}

	static synchronized SwitchPoint createNewGlobalSwitchPoint() {
		globalSwitchPoint = new SwitchPoint();
		return globalSwitchPoint;
	}

	@Override
	public BoxedCallSite<K, V> clone() {
		try {
			return (BoxedCallSite<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Map<K, V> getCache() {
		return this.cache;
	}

	public synchronized MethodHandle guard(MethodHandle guard, MethodHandle target, MethodHandle initialFallback) {
		if (this.lastHandles.size() >= this.maxNumberOfCallSites) {
			this.lastHandles.removeLast();
		}

		this.lastHandles.addFirst(new Pair<MethodHandle, MethodHandle>(guard, target));

		MethodHandle guardedHandle = null;
		MethodHandle fallbackHandle = initialFallback;

		Iterator<Pair<MethodHandle, MethodHandle>> it = this.lastHandles.descendingIterator();
		while (it.hasNext()) {
			Pair<MethodHandle, MethodHandle> pair = it.next();
			guardedHandle = MethodHandles.guardWithTest(pair.getX(), pair.getY(), fallbackHandle);
			fallbackHandle = guardedHandle;
		}

		return guardedHandle;
	}

	public MethodHandle switchpoint(Invalidator invalidator, MethodHandle target, MethodHandle fallback) {
		SwitchPoint switchPoint = new SwitchPoint();
		invalidator.setInvalidationListener(new InvalidationListener(switchPoint));
		return switchPoint.guardWithTest(target, fallback);
	}
}
