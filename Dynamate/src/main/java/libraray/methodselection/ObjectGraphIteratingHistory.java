package libraray.methodselection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import library.Pair;

public class ObjectGraphIteratingHistory<T, O> implements Iterable<Pair<T, O>> {

	private List<Pair<T, O>> history = new ArrayList<>();

	public void addHistoryItem(Pair<T, O> item) {
		this.history.add(0, item);
	}

	@Override
	public Iterator<Pair<T, O>> iterator() {
		return this.history.iterator();
	}

	public int size() {
		return this.history.size();
	}

	public Pair<T, O> get(int index) {
		if (index >= this.history.size()) {
			return null;
		}

		return this.history.get(index);
	}

	public Pair<T, O> getLast() {
		return this.history.get(this.history.size() - 1);
	}

	public Object[] getValues(int max) {
		Object[] array = new Object[max];

		for (int i = 0; i < max; i++) {
			Pair<T, O> pair = this.history.get(i);
			array[i] = pair.getY();
		}

		return array;
	}

	@Override
	public String toString() {
		return "GraphIteratingHistory [history=" + this.history + "]";
	}
}
