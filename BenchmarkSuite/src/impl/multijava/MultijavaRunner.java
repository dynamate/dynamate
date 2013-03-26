package impl.multijava;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class MultijavaRunner extends BenchmarkRunner {

	public MultijavaRunner(String... classpath) {
		super(null, BenchmarkRunner.WORKSPACE, false, classpath);

		this.registerBenchmark(new Benchmark("Constant List of Shapes", "multijava.multipledispatch.ConstantClient", "100000", "m2"));
		this.registerBenchmark(new Benchmark("Constant List of Shapes", "multijava.multipledispatch.ConstantClient", "1000000", "m2"));
		this.registerBenchmark(new Benchmark("Constant List of Shapes", "multijava.multipledispatch.ConstantClient", "10000000", "m2"));

		this.registerBenchmark(new Benchmark("Constant List of Shapes", "multijava.multipledispatch.ConstantClient", "100000", "m5"));
		this.registerBenchmark(new Benchmark("Constant List of Shapes", "multijava.multipledispatch.ConstantClient", "1000000", "m5"));
		this.registerBenchmark(new Benchmark("Constant List of Shapes", "multijava.multipledispatch.ConstantClient", "10000000", "m5"));

		this.registerBenchmark(new Benchmark("Shuffled List of Shapes", "multijava.multipledispatch.ShufflingClient", "100000", "m2"));
		this.registerBenchmark(new Benchmark("Shuffled List of Shapes", "multijava.multipledispatch.ShufflingClient", "1000000", "m2"));
		this.registerBenchmark(new Benchmark("Shuffled List of Shapes", "multijava.multipledispatch.ShufflingClient", "10000000", "m2"));

		this.registerBenchmark(new Benchmark("Shuffled List of Shapes", "multijava.multipledispatch.ShufflingClient", "100000", "m5"));
		this.registerBenchmark(new Benchmark("Shuffled List of Shapes", "multijava.multipledispatch.ShufflingClient", "1000000", "m5"));
		this.registerBenchmark(new Benchmark("Shuffled List of Shapes", "multijava.multipledispatch.ShufflingClient", "10000000", "m5"));

		this.registerBenchmark(new Benchmark("Random Value Dispatch", "multijava.valuedispatch.RandomValueDispatchClient", "100000"));
		this.registerBenchmark(new Benchmark("Random Value Dispatch", "multijava.valuedispatch.RandomValueDispatchClient", "1000000"));
		this.registerBenchmark(new Benchmark("Random Value Dispatch", "multijava.valuedispatch.RandomValueDispatchClient", "10000000"));
	}

	public static void main(String[] args) {
		try {
			MultijavaRunner runner1 = new MultijavaRunner("Multijava_original/bin");

			runner1.start();

			MultijavaRunner runner2 = new MultijavaRunner("Dynamate/bin", "Dynamate/classes", "MultijavaInvoke/bin", "Dynamate/lib/slf4j-api-1.6.6.jar",
					"Dynamate/lib/slf4j-simple-1.6.6.jar");

			runner2.start();

			new BenchmarkResultPrinter(runner1, runner2).printResults(System.out);
			// new BenchmarkResultPrinter(runner1, runner2).printResults(new PrintStream(new FileOutputStream("multijava.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
