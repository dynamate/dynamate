package multijava.multipledispatch;

import invoker.SourceCodeInvoker;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import library.impl.multijava.Bootstrap;

public class ShufflingClient {

	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);
		String method = args[1];

		Shape circle = new Circle();
		Shape rectangle = new Rectangle();
		Shape pentagon = new Pentagon();
		Shape ellipse = new Ellipse();

		List<Shape> shapes = new ArrayList<Shape>(6);
		shapes.add(circle);
		shapes.add(rectangle);
		shapes.add(pentagon);
		shapes.add(ellipse);
		shapes.add(pentagon);
		shapes.add(ellipse);

		if (method.equals("m2")) {
			for (int i = 0; i < n; i++) {
				m2(shapes);
			}
		} else if (method.equals("m5")) {
			for (int i = 0; i < n; i++) {
				m5(shapes);
			}
		}
	}

	static final MethodType m2Type = MethodType.methodType(boolean.class, Shape.class, Shape.class, Shape.class);

	private static void m2(List<Shape> shapes) {
		List<Shape> shapeList = new ArrayList<Shape>(shapes);

		Collections.shuffle(shapeList);

		Shape rcv = shapeList.get(0);
		Shape p0 = shapeList.get(1);
		Shape p1 = shapeList.get(2);

		SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrapMultiDispatch", "m2", m2Type, rcv, p0, p1);
		// rcv.intersects(p0, p1);
	}

	static final MethodType m5Type = MethodType.methodType(boolean.class, Shape.class, Shape.class, Shape.class, Shape.class, Shape.class, Shape.class);

	private static void m5(List<Shape> shapes) {
		List<Shape> shapeList = new ArrayList<Shape>(shapes);
		Collections.shuffle(shapeList);

		Shape rcv = shapeList.get(0);
		Shape p0 = shapeList.get(1);
		Shape p1 = shapeList.get(2);
		Shape p2 = shapeList.get(3);
		Shape p3 = shapeList.get(4);
		Shape p4 = shapeList.get(5);

		SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrapMultiDispatch", "m5", m5Type, rcv, p0, p1, p2, p3, p4);
		// rcv.intersects(p0, p1);
	}
}
