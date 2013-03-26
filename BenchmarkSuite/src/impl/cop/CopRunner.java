package impl.cop;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class CopRunner extends BenchmarkRunner {

	public CopRunner(String... classpath) {
		super(null, BenchmarkRunner.WORKSPACE, false, classpath);

		this.registerBenchmark(new Benchmark("Direct, No Layers", "app.DirectApp", "10000"));
		this.registerBenchmark(new Benchmark("Direct, No Layers", "app.DirectApp", "100000"));
		this.registerBenchmark(new Benchmark("Constant, 3 Layers", "app.ConstantApp", "10000"));
		this.registerBenchmark(new Benchmark("Constant, 3 Layers", "app.ConstantApp", "100000"));
		this.registerBenchmark(new Benchmark("Sequential, 1-5 Layers", "app.SequentialApp", "10000"));
		this.registerBenchmark(new Benchmark("Sequential, 1-5 Layers", "app.SequentialApp", "100000"));
		this.registerBenchmark(new Benchmark("Alternating, 1-5 Layers", "app.AlternatingApp", "1000"));
		this.registerBenchmark(new Benchmark("Alternating, 1-5 Layers", "app.AlternatingApp", "10000"));
	}

	public static void main(String[] args) {
		try {

			CopRunner runner1 = new CopRunner("JCop/aspectjtools.jar", "JCop/bin");
			runner1.start();

			CopRunner runner2 = new CopRunner("Dynamate/bin", "Dynamate/classes", "COPInvoke/bin", "Dynamate/lib/slf4j-api-1.6.6.jar", "Dynamate/lib/slf4j-simple-1.6.6.jar");
			runner2.start();

			BenchmarkResultPrinter resultPrinter = new BenchmarkResultPrinter(runner1, runner2);
			resultPrinter.printResults(System.out);
			// resultPrinter.printResults(new PrintStream(new FileOutputStream("cop.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
