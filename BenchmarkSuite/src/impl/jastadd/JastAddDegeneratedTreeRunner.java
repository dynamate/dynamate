package impl.jastadd;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class JastAddDegeneratedTreeRunner extends BenchmarkRunner {

	public JastAddDegeneratedTreeRunner(String... classpath) {
		super(null, BenchmarkRunner.WORKSPACE, false, classpath);

		this.registerBenchmark(new Benchmark("Degenerated Tree", "client.DegeneratedTreeClient", "5"));
		this.registerBenchmark(new Benchmark("Degenerated Tree", "client.DegeneratedTreeClient", "50"));
		this.registerBenchmark(new Benchmark("Degenerated Tree", "client.DegeneratedTreeClient", "100"));
		this.registerBenchmark(new Benchmark("Degenerated Tree", "client.DegeneratedTreeClient", "500"));
	}

	public static void main(String[] args) {
		try {
			JastAddDegeneratedTreeRunner runner1 = new JastAddDegeneratedTreeRunner("DegeneratedTree_original/bin");
			runner1.start();

			JastAddDegeneratedTreeRunner runner2 = new JastAddDegeneratedTreeRunner("Dynamate/bin", "Dynamate/classes", "JastAddInvoke/bin", "DegeneratedTree/bin",
					"Dynamate/lib/slf4j-api-1.6.6.jar", "Dynamate/lib/slf4j-simple-1.6.6.jar");
			runner2.start();

			BenchmarkResultPrinter resultPrinter = new BenchmarkResultPrinter(runner1, runner2);
			resultPrinter.printResults(System.out);
			// resultPrinter.printResults(new PrintStream(new FileOutputStream("jastAdd_degeneratedTree.csv")));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
