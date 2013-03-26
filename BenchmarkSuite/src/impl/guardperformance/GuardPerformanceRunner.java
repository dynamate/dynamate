package impl.guardperformance;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class GuardPerformanceRunner extends BenchmarkRunner {

	public GuardPerformanceRunner(String... classpath) {
		super(null, BenchmarkRunner.WORKSPACE, false, classpath);

		for (int p = 0; p <= 100; p += 10) {
			this.registerBenchmark(new Benchmark(Integer.toString(p), "guardperformance.Client", Integer.toString(p)));
		}
	}

	static GuardPerformanceRunner[] run(int var) throws IOException {
		GuardPerformanceRunner[] runners = new GuardPerformanceRunner[12];
		for (int i = 0; i < 12; i++) {
			runners[i] = new GuardPerformanceRunner("Dynamate/bin", "Dynamate/classes", "Dynamate/lib/slf4j-api-1.6.6.jar", "Dynamate/lib/slf4j-simple-1.6.6.jar");
			runners[i].addSystemProperty("variability", Integer.toString(var));
			runners[i].addSystemProperty("scenario", Integer.toString(i));
			runners[i].start();
		}

		return runners;
	}

	public static void main(String[] args) {
		try {

			GuardPerformanceRunner[] runners = run(10);
			BenchmarkResultPrinter resultPrinter = new BenchmarkResultPrinter(runners);
			resultPrinter.setOmitArguments(true);
			resultPrinter.printResults(System.out);
			// resultPrinter.printResults(new PrintStream(new FileOutputStream("guardperformance.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
