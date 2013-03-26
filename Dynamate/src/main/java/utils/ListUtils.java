package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ListUtils {
	/**
	 * Returns a range of integers including both specified values.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<Integer> range(int start, int end) {
		ArrayList<Integer> list = new ArrayList<>((end - start) + 1);

		for (int i = start; i <= end; i++) {
			list.add(i);
		}

		return list;
	}

	public static int[] toIntArray(Collection<? extends Number> list) {
		int[] array = new int[list.size()];
		Iterator<? extends Number> it = list.iterator();

		for (int i = 0; i < array.length; i++) {
			array[i] = it.next().intValue();
		}

		return array;
	}

	public static double[] toDoubleArray(Collection<? extends Number> list) {
		double[] array = new double[list.size()];
		Iterator<? extends Number> it = list.iterator();

		for (int i = 0; i < array.length; i++) {
			array[i] = it.next().doubleValue();
		}

		return array;
	}
}
