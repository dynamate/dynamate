package library.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LanguageTestCase {

	protected abstract void invokeMainMethod(String[] arguments);

	protected abstract String getTestFileEnding();

	protected abstract void assertEquals(String testCase, String expected, String actual);

	protected abstract void assertEquals(String testCase, byte[] expected, byte[] actual);

	protected String getResourcesPath() {
		return "src/test/resources";
	}

	protected String getTestFilePath(String testCase) {
		return getResourcesPath() + "/" + testCase + "." + getTestFileEnding();
	}

	protected String getExpectedOutputFilePath(String testCase) {
		return getResourcesPath() + "/" + testCase + "." + getExpectedOutputFileEnding();
	}

	protected String getInputFilePath(String testCase) {
		return getResourcesPath() + "/" + testCase + "." + getInputFileEnding();
	}

	protected String getInputFileEnding() {
		return "in";
	}

	protected String getExpectedOutputFileEnding() {
		return "expectedOutput";
	}

	protected byte[] getExpectedOutput(String testCase) throws IOException {
		InputStream fileInputStream = new BufferedInputStream(new FileInputStream(getExpectedOutputFilePath(testCase)));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int c = fileInputStream.read();

		do {
			baos.write(c);
		} while ((c = fileInputStream.read()) != -1);

		fileInputStream.close();
		return baos.toByteArray();
	}

	protected void invokeTestCase(String testCase, boolean useStdIn, int languageArgumentsStartIndex, String... additionalArguments) {
		try {
			if (useStdIn) {
				System.setIn(new BufferedInputStream(new FileInputStream(getInputFilePath(testCase))));
			}

			List<String> arguments = new ArrayList<String>(Arrays.asList(additionalArguments));
			String filePath = getTestFilePath(testCase);
			arguments.add(languageArgumentsStartIndex, filePath);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			System.setOut(new PrintStream(baos));

			invokeMainMethod(arguments.toArray(new String[arguments.size()]));

			this.assertEquals(testCase, getExpectedOutput(testCase), baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	protected void invokeTestCase(String testCase, String... additionalArguments) {
		invokeTestCase(testCase, false, 0, additionalArguments);
	}

	protected void invokeTestCase(String testCase, int languageArgumentsStartIndex, String... additionalArguments) {
		invokeTestCase(testCase, false, languageArgumentsStartIndex, additionalArguments);
	}

	protected void invokeTestCase(String testCase, boolean useStdIn, String... additionalArguments) {
		invokeTestCase(testCase, useStdIn, 0, additionalArguments);
	}

	public abstract void fail(String message);
}
