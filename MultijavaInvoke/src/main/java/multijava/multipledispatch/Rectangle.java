package multijava.multipledispatch;

public class Rectangle extends Shape {
	public boolean m1(Shape shape, Circle circle) {
		// System.out.println("Rectangle.intersects(Shape, Circle)");
		return false;
	}

	public boolean m2(Shape shape, Circle circle) {
		// System.out.println("Rectangle.intersects(Shape, Circle)");
		return false;
	}
}
