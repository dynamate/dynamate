package multijava.valuedispatch;

public class Foo {

	@ValueDispatchable({ 1, 2, 3 })
	public void foo(int x, int y) {
	}

	public void foo$1(@OnValue("1") int x, @OnValue("1") int y) {
	}

	public void foo$2(@OnValue("1") int x, @OnValue("2") int y) {
	}

	public void foo$3(@OnValue("3") int x, int y) {
	}
}
