package impl.groovy;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class GroovyRunner extends BenchmarkRunner {

	public GroovyRunner(String... classpath) {
		super(false, "groovy.ui.GroovyMain", BenchmarkRunner.WORKSPACE, classpath);

		this.registerBenchmark(new Benchmark("Ackermann", "ackermann", new String[] { "--indy" }, "10"));
		// this.registerBenchmark(new Benchmark("Ackermann", "ackermann", new String[] { "--indy" }, "1"));

		this.registerBenchmark(new Benchmark("mandelbrot", "benchmark1", new String[] { "--indy" }, "1000"));
		// this.registerBenchmark(new Benchmark("mandelbrot", "benchmark1", new String[] { "--indy" }, "10000"));

		this.registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", new String[] { "--indy" }, "10"));
		// this.registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", new String[] { "--indy" }, "10")));

		this.registerBenchmark(new Benchmark("spectral-norm", "benchmark4", new String[] { "--indy" }, "500"));
		// this.registerBenchmark(new Benchmark("spectral-norm", "benchmark4", new String[] { "--indy" }, "500"));

		this.registerBenchmark(new Benchmark("binary-trees", "benchmark6", new String[] { "--indy" }, "12"));
		// this.registerBenchmark(new Benchmark("binary-trees", "benchmark6", new String[] { "--indy" }, "16"));

		this.registerBenchmark(new Benchmark("regex-dna", "benchmark7", true, new String[] { "--indy" }, "50000"));
		// this.registerBenchmark(new Benchmark("regex-dna", "benchmark7", true, new String[] { "--indy" }, "500000")));

		this.registerBenchmark(new Benchmark("nSieve", "nsieve", new String[] { "--indy" }, "10"));
		// this.registerBenchmark(new Benchmark("nSieve", "nsieve", new String[] { "--indy" }, "12"));

		this.registerBenchmark(new Benchmark("fibonacci", "fibo", new String[] { "--indy" }, "40"));
		// this.registerBenchmark(new Benchmark("fibonacci", "fibo", new String[] { "--indy" }, "45"));

		this.registerBenchmark(new Benchmark("alternating dispatch", "benchmark11", new String[] { "--indy" }, "100000"));
		// this.registerBenchmark(new Benchmark("alternating dispatch", "benchmark11", new String[] { "--indy" }, "1000000"));

		this.registerBenchmark(new Benchmark("sequential dispatch", "benchmark12", new String[] { "--indy" }, "100000"));
		// this.registerBenchmark(new Benchmark("sequential dispatch", "benchmark12", new String[] { "--indy" }, "1000000"));
	}

	@Override
	protected String getResourcesPath() {
		return "/GroovyInvoke/src/test/resources/benchmark";
	}

	@Override
	protected String getTestFileEnding() {
		return "groovy";
	}

	public static void main(String[] args) {
		try {
			GroovyRunner runner1 = new GroovyRunner("Dynamate/bin", "Dynamate/classes", "GroovyInvoke/bin", "GroovyInvoke/lib/groovy-all-2.0.0-indy.jar",
					"Dynamate/lib/slf4j-api-1.6.6.jar", "Dynamate/lib/slf4j-simple-1.6.6.jar");

			runner1.addSystemProperty("skipIdLibrary", "true");
			runner1.start();

			GroovyRunner runner2 = new GroovyRunner("Dynamate/bin", "Dynamate/classes", "GroovyInvoke/bin", "GroovyInvoke/lib/groovy-all-2.0.0-indy.jar",
					"Dynamate/lib/slf4j-api-1.6.6.jar", "Dynamate/lib/slf4j-simple-1.6.6.jar");
			runner2.addSystemProperty("skipIdLibrary", "false");
			runner2.start();

			BenchmarkResultPrinter benchmarkResultPrinter = new BenchmarkResultPrinter(runner1, runner2);
			benchmarkResultPrinter.printResults(System.out);
			// benchmarkResultPrinter.printResults(new PrintStream(new FileOutputStream("groovy.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
