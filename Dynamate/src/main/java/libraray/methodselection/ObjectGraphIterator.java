package libraray.methodselection;

import library.Pair;

public interface ObjectGraphIterator<T, O> {
	public Pair<T, O> next(T type, O object);

	public boolean hasNext(T type, O object);
}
