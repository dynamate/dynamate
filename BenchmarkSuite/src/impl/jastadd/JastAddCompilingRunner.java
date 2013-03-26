package impl.jastadd;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class JastAddCompilingRunner extends BenchmarkRunner {

	public JastAddCompilingRunner(String... classpath) {
		super(null, BenchmarkRunner.WORKSPACE, false, classpath);

		this.registerBenchmark(new Benchmark("AspectJ Lib Compiling", "org.jastadd.jastaddj.JavaCompiler", "-d", "bin", "@" + BenchmarkRunner.WORKSPACE
				+ "BenchmarkSuite/resources/list1"));
		this.registerBenchmark(new Benchmark("Apache commons Lib Compiling", "org.jastadd.jastaddj.JavaCompiler", "-d", "bin", "@" + BenchmarkRunner.WORKSPACE
				+ "BenchmarkSuite/resources/list2"));
	}

	public static void main(String[] args) {
		try {
			JastAddCompilingRunner runner1 = new JastAddCompilingRunner("JastAddJ_original/Java7Backend");
			runner1.start();

			JastAddCompilingRunner runner2 = new JastAddCompilingRunner("Dynamate/bin", "Dynamate/classes", "JastAddInvoke/bin", "JastAddJ/Java7Backend",
					"Dynamate/lib/slf4j-api-1.6.6.jar", "Dynamate/lib/slf4j-simple-1.6.6.jar");
			runner2.start();

			BenchmarkResultPrinter resultPrinter = new BenchmarkResultPrinter(runner1, runner2);
			resultPrinter.printResults(System.out);
			// resultPrinter.printResults(new PrintStream(new FileOutputStream("jastAdd.csv")));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
