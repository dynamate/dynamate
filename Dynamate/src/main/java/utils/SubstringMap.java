package utils;

import java.util.HashMap;

public class SubstringMap<V> extends HashMap<String, V> {

	@Override
	public boolean containsKey(Object key) {
		return this.get(key) != null;
	}

	@Override
	public V get(Object key) {
		String stringKey = (String) key;

		char[] chars = stringKey.toCharArray();
		StringBuilder sb = new StringBuilder(stringKey);

		for (int i = chars.length - 1; i >= 0; i--) {
			V value = super.get(sb.toString());

			if (value != null) {
				return value;
			}

			sb.deleteCharAt(i);
		}

		return null;
	}
}
