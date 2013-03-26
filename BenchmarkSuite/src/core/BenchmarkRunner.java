package core;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.StatUtils;

import utils.ListUtils;

public abstract class BenchmarkRunner {

	public static final String WORKSPACE;

	private static final int NUMBER_OF_RUNS;

	static {
		String workspace = System.getProperty("WORKSPACE");
		WORKSPACE = workspace != null ? workspace : "/home/kamil/workspace/master";

		String numberOfRuns = System.getProperty("NUMBER_OF_RUNS");
		NUMBER_OF_RUNS = numberOfRuns != null ? Integer.parseInt(numberOfRuns) : 1;
	}

	private List<Benchmark> benchmarks = new LinkedList<Benchmark>();

	private String[] classpath;

	private String executionerClass;

	private String workingDirectory;

	private Map<String, String> systemProperties = new HashMap<>();

	private List<Long> times;

	private boolean relativeBenchmarkPaths;

	private boolean useBootstrapClasspath;

	public BenchmarkRunner(String executionerClass, String workingDirectory, String... classpath) {
		this(true, executionerClass, workingDirectory, true, classpath);
	}

	public BenchmarkRunner(boolean useBootstrapClasspath, String executionerClass, String workingDirectory, String... classpath) {
		this(useBootstrapClasspath, executionerClass, workingDirectory, true, classpath);
	}

	public BenchmarkRunner(String executionerClass, String workingDirectory, boolean relativeBenchmarkPaths, String... classpath) {
		this(true, executionerClass, workingDirectory, relativeBenchmarkPaths, classpath);
	}

	public BenchmarkRunner(boolean useBootstrapClasspath, String executionerClass, String workingDirectory, boolean relativeBenchmarkPaths, String... classpath) {
		this.useBootstrapClasspath = useBootstrapClasspath;
		this.relativeBenchmarkPaths = relativeBenchmarkPaths;
		this.classpath = classpath;
		this.executionerClass = executionerClass;
		this.workingDirectory = workingDirectory;
	}

	protected void registerBenchmark(Benchmark benchmark) {
		this.benchmarks.add(benchmark);
	}

	protected String getTestFileEnding() {
		return null;
	}

	protected String getResourcesPath() {
		return "/JRubyInvoke/src/test/resources/benchmark";
	}

	protected String getTestFilePath(String testCase) {
		return this.getResourcesPath() + "/" + testCase + (this.getTestFileEnding() == null ? "" : "." + this.getTestFileEnding());
	}

	protected String getInputFilePath(String testCase) {
		return this.getResourcesPath() + "/" + testCase + "." + this.getInputFileEnding();
	}

	protected String getInputFileEnding() {
		return "in";
	}

	protected long invokeBenchmark(String name, String relativePath, boolean useStdIn, List<String> runtimeArguments, List<String> arguments) {
		String filePath = this.relativeBenchmarkPaths ? this.workingDirectory + this.getTestFilePath(relativePath) : relativePath;

		List<String> allArguments = new ArrayList<String>(runtimeArguments.size() + 1 + arguments.size());

		allArguments.addAll(runtimeArguments);
		allArguments.add(filePath);
		allArguments.addAll(arguments);

		return this.measureTimes(name, relativePath, arguments, allArguments, useStdIn);
	}

	private long measureTimes(String name, String path, List<String> arguments, List<String> allArguments, boolean useStdIn) {
		List<Long> times = new ArrayList<Long>(BenchmarkRunner.NUMBER_OF_RUNS);

		for (int i = 0; i < BenchmarkRunner.NUMBER_OF_RUNS; i++) {

			long time = this.measureTime(arguments, allArguments, useStdIn);
			long timeInMillis = TimeUnit.NANOSECONDS.toMillis(time);

			times.add(timeInMillis);
		}

		long avg = this.calculateMean(times);
		return avg;
	}

	private long measureTime(List<String> arguments, List<String> allArguments, boolean useStdIn) {
		StringBuilder systemProperties = new StringBuilder();
		for (Entry<String, String> entry : this.systemProperties.entrySet()) {
			systemProperties.append("-D").append(entry.getKey()).append('=').append(entry.getValue()).append(' ');
		}

		String cp = this.useBootstrapClasspath ? "-Xbootclasspath/a:" : "-cp ";

		String command = "java " + systemProperties.toString() + cp + this.getAbsoluteClasspath(this.getClasspath())
				+ (this.getMainMethod() == null ? "" : " " + this.getMainMethod());

		String[] commandArray = command.split(" ");

		List<String> commandList = new ArrayList<String>(commandArray.length + allArguments.size());
		commandList.addAll(Arrays.asList(commandArray));
		commandList.addAll(allArguments);

		ByteArrayOutputStream errorStream = null;

		long t0 = System.nanoTime();
		long t1 = 0;

		try {
			System.out.println("Invoked Command: " + commandList.toString().replace(", ", " "));

			Process process = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]), null);

			errorStream = new ByteArrayOutputStream();
			IOUtils.asynchronousTransfer(process.getErrorStream(), errorStream);

			if (useStdIn) {
				String input = arguments.get(0);
				IOUtils.asynchronousTransfer(new FileInputStream(this.workingDirectory + this.getInputFilePath(input)), process.getOutputStream());
			}

			IOUtils.asynchronousTransfer(process.getInputStream(), new IOUtils.NullStream());

			process.waitFor();
			t1 = System.nanoTime();

			// System.err.write(errorStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t1 - t0;
	}

	private String getAbsoluteClasspath(String[] classpath2) {
		StringBuilder sb = new StringBuilder();
		for (String cp : this.classpath) {
			sb.append(this.workingDirectory).append('/').append(cp).append(':');
		}

		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	protected String getMainMethod() {
		return this.executionerClass;
	}

	protected String[] getClasspath() {
		return this.classpath;
	}

	private long calculateMean(List<Long> times) {
		return (long) StatUtils.mean(ListUtils.toDoubleArray(times));
	}

	public void start() throws IOException {
		this.times = new ArrayList<>(this.benchmarks.size());

		for (Benchmark benchmark : this.benchmarks) {
			long time = this.invokeBenchmark(benchmark.getName(), benchmark.getPath(), benchmark.isUsingStandardIn(), benchmark.getRuntimeArguments(), benchmark.getArguments());

			this.times.add(time);
		}
	}

	public void addSystemProperty(String key, String value) {
		this.systemProperties.put(key, value);
	}

	public void setSystemProperties(Map<String, String> systemProperties) {
		this.systemProperties = systemProperties;
	}

	public List<Long> getTimes() {
		return this.times;
	}

	public List<Benchmark> getBenchmarks() {
		return this.benchmarks;
	}
}
