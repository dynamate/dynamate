package guardperformance;

import libraray.methodselection.ObjectGraphIterator;
import library.Pair;

public class JavaClassHierrachyIterator implements ObjectGraphIterator<Class<?>, Object> {

	@Override
	public Pair<Class<?>, Object> next(Class<?> type, Object object) {
		return new Pair<Class<?>, Object>(type.getSuperclass(), object);
	}

	@Override
	public boolean hasNext(Class<?> type, Object object) {
		return type.getSuperclass() != null;
	}
}