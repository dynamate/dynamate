package libraray.adaptation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BasicArgumentInjector implements ArgumentInjector {

	private Set<Class<?>> nullInjections = new HashSet<>();
	private Set<Class<?>> instanceInjections = new HashSet<>();
	private Map<Class<?>, Object> constantInjections = new HashMap<>();

	public void registerNullInjection(Class<?> clazz) {
		this.nullInjections.add(clazz);
	}

	public void registerInstanceInjection(Class<?> clazz) {
		this.instanceInjections.add(clazz);
	}

	public void registerConstantInjection(Class<?> clazz, Object constant) {
		this.constantInjections.put(clazz, constant);
	}

	@Override
	public boolean isInjectable(Class<?> clazz) {
		return this.nullInjections.contains(clazz) || this.instanceInjections.contains(clazz) || this.constantInjections.containsKey(clazz);
	}

	@Override
	public Object injectValue(Class<?> clazz) {
		if (this.nullInjections.contains(clazz)) {
			return null;
		}

		if (this.instanceInjections.contains(clazz)) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (this.constantInjections.containsKey(clazz)) {
			return this.constantInjections.get(clazz);
		}

		return Exception.class;
	}
}