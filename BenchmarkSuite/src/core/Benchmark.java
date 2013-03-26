package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Benchmark {

	private String name;

	private String path;

	private List<String> arguments = new ArrayList<String>();

	private List<String> runtimeArguments = new ArrayList<String>();

	private boolean usingStandardIn = false;

	public Benchmark(String name, String path, String... arguments) {
		this(name, path, false, new String[0], arguments);
	}

	public Benchmark(String name, String path, String[] runtimeArguments, String... arguments) {
		this(name, path, false, runtimeArguments, arguments);
	}

	public Benchmark(String name, String path, boolean usingStandardIn, String... arguments) {
		this(name, path, usingStandardIn, new String[0], arguments);
	}

	public Benchmark(String name, String path, boolean usingStandardIn, String[] runtimeArguments, String... arguments) {
		this.name = name;
		this.path = path;
		this.runtimeArguments = new ArrayList<String>(Arrays.asList(runtimeArguments));
		this.arguments = new ArrayList<String>(Arrays.asList(arguments));
		this.usingStandardIn = usingStandardIn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getArguments() {
		return this.arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public List<String> getRuntimeArguments() {
		return this.runtimeArguments;
	}

	public void setRuntimeArguments(List<String> runtimeArguments) {
		this.runtimeArguments = runtimeArguments;
	}

	public boolean isUsingStandardIn() {
		return this.usingStandardIn;
	}

	public void setUsingStandardIn(boolean usingStandardIn) {
		this.usingStandardIn = usingStandardIn;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
