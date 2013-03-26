package app;

import core.Composition;
import core.Layer;

public class AlternatingApp {

	private static Person p = new Person("Hans", "Street no 6");

	private static Layer Address1 = new Address1();
	private static Layer Address2 = new Address2();
	private static Layer Address3 = new Address3();
	private static Layer Address4 = new Address4();
	private static Layer Address5 = new Address5();

	public static void main(String[] args) {
		int runs = Integer.parseInt(args[0]);

		for (int i = 0; i < runs; i++) {
			p.toString();

			Composition.with(Address1);
			p.toString();
			Composition.without();

			Composition.with(Address1, Address2);
			p.toString();
			Composition.without();

			Composition.with(Address1, Address2, Address3);
			p.toString();
			Composition.without();

			Composition.with(Address1, Address2, Address3, Address4);
			p.toString();
			Composition.without();

			Composition.with(Address1, Address2, Address3, Address4, Address5);
			p.toString();
			Composition.without();
		}
	}

}
