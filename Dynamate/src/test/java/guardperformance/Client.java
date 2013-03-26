package guardperformance;

import invoker.SourceCodeInvoker;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client {

	public static List<Integer> buildPropabilityList(int p, int num, int var) {
		int q = (100 - p) * num;
		List<Integer> list = new ArrayList<Integer>(100 * num);

		for (int i = 0; i < q; i++) {
			list.add(0);
		}

		int total = (100 * num) - q;

		for (int j = 1; j <= var; j++) {
			for (int i = 0; i < (total / var); i++) {
				list.add(j);
			}
		}

		Collections.shuffle(list);
		return list;
	}

	public static void main(String[] args) {
		int p = Integer.parseInt(args[0]);
		int var = Integer.parseInt(System.getProperty("variability"));
		String scenario = System.getProperty("scenario");
		List<Integer> list = buildPropabilityList(p, 50000, var);

		for (Integer i : list) {
			SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrap" + scenario, "m", MethodType.methodType(void.class, int.class), i);
		}
	}
}
