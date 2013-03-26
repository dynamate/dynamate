package multijava.multipledispatch;

public class Shape {
	public boolean m1(Shape shape1) {
		// System.out.println("Shape.intersects(Shape, Shape)");
		return false;
	}

	public boolean m1(Rectangle shape1) {
		// System.out.println("Shape.intersects(Rectangle, Pentagon)");
		return false;
	}

	public boolean m2(Shape shape1, Shape shape2) {
		// System.out.println("Shape.intersects(Shape, Shape)");
		return false;
	}

	public boolean m2(Rectangle shape1, Pentagon shape2) {
		// System.out.println("Shape.intersects(Rectangle, Pentagon)");
		return false;
	}

	public boolean m5(Shape shape1, Shape shape2, Shape shape3, Shape shape4, Shape shape5) {
		return false;
	}

	public boolean m5(Shape shape1, Ellipse ellipse, Pentagon pentagon, Shape shape4, Circle circle) {
		return false;
	}
}
