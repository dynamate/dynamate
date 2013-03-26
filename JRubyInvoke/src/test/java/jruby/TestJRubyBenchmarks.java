package jruby;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;
import library.test.LanguageTestCase;

import org.junit.Test;

public class TestJRubyBenchmarks extends LanguageTestCase {

	@Override
	protected String getTestFileEnding() {
		return "rb";
	}

	@Override
	protected void invokeMainMethod(String[] arguments) {
		System.err.println(Arrays.toString(arguments));
		org.jruby.Main.main(arguments);
	}

	@Override
	protected void assertEquals(String testCase, String expected, String actual) {
		Assert.assertEquals(testCase, expected, actual);
	}

	@Test
	public void test1() throws IOException {
		this.invokeTestCase("benchmark/benchmark1", "200");
	}

	@Test
	public void test2() throws IOException {
		this.invokeTestCase("benchmark/benchmark2", "7");
	}

	@Test
	public void test3() throws IOException {
		this.invokeTestCase("benchmark/benchmark3", "1000");
	}

	@Test
	public void test4() throws IOException {
		this.invokeTestCase("benchmark/benchmark4", "100");
	}

	@Test
	public void test5() throws IOException {
		this.invokeTestCase("benchmark/benchmark5", true);
	}

	@Test
	public void test6() throws IOException {
		this.invokeTestCase("benchmark/benchmark6", "10");
	}

	@Test
	public void test7() throws IOException {
		this.invokeTestCase("benchmark/benchmark7", true);
	}

	@Test
	public void test8() throws IOException {
		this.invokeTestCase("benchmark/benchmark8", "27");
	}

	@Test
	public void test9() throws IOException {
		this.invokeTestCase("benchmark/benchmark9", "1000");
	}

	@Test
	public void test10() throws IOException {
		this.invokeTestCase("benchmark/benchmark10", true);
	}

	@Override
	public void fail(String message) {
		Assert.fail(message);
	}

	@Override
	protected String getExpectedOutputFileEnding() {
		return "out";
	}

	public static void main(String[] args) {
		new TestJRubyBenchmarks().invokeTestCase("benchmark/benchmark2", false, "7");
	}

	@Override
	protected void assertEquals(String testCase, byte[] expected, byte[] actual) {
		org.junit.Assert.assertArrayEquals(testCase, expected, actual);
	}
}
