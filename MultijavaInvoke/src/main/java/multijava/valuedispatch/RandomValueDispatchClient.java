package multijava.valuedispatch;

import invoker.SourceCodeInvoker;

import java.lang.invoke.MethodType;
import java.util.Random;

import library.impl.multijava.Bootstrap;

public class RandomValueDispatchClient {
	public static void main(String[] args) {

		int n = Integer.parseInt(args[0]);
		Foo foo = new Foo();
		Random r = new Random();

		for (int i = 0; i < n; i++) {
			perform(foo, r.nextInt(5), r.nextInt(5));
		}
	}

	public static void perform(Foo foo, int x, int y) {
		SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrapValueDispatch", "foo", MethodType.methodType(void.class, Foo.class, int.class, int.class), foo, x, y);
	}
}
