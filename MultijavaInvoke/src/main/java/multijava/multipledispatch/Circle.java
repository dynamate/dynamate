package multijava.multipledispatch;

public class Circle extends Shape {
	@Override
	public boolean m1(Rectangle rectangle) {
		return false;
	}

	@Override
	public boolean m2(Rectangle rectangle, Pentagon pentagon) {
		return false;
	}

	public boolean m5(Rectangle rectangle, Pentagon pentagon, Circle cirle, Shape shape, Ellipse ellipse) {
		return false;
	}
}
