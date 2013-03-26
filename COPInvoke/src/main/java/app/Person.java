package app;

import invoker.SourceCodeInvoker;

import java.lang.invoke.MethodType;

import library.impl.cop.Bootstrap;

public class Person {
	private String name;
	
	private String address;
	
	public Person(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrap", "Person$$toString", MethodType.methodType(String.class, Person.class), this);
	}
	
	public String toString$$Address1() {
		return SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrap", "proceed:Person$$toString:Address1", MethodType.methodType(String.class, Person.class), this) + ", " + getAddress();
	}
	
	public String toString$$Address2() {
		return SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrap", "proceed:Person$$toString:Address2", MethodType.methodType(String.class, Person.class), this) + ", " + getAddress();
	}
	
	public String toString$$Address3() {
		return SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrap", "proceed:Person$$toString:Address3", MethodType.methodType(String.class, Person.class), this) + ", " + getAddress();
	}
	
	public String toString$$Address4() {
		return SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrap", "proceed:Person$$toString:Address4", MethodType.methodType(String.class, Person.class), this) + ", " + getAddress();
	}
	
	public String toString$$Address5() {
		return SourceCodeInvoker.invokeDynamic(Bootstrap.class, "bootstrap", "proceed:Person$$toString:Address5", MethodType.methodType(String.class, Person.class), this) + ", " + getAddress();
	}
	
	public String toString$$base() {
		return getName();
	}
}
