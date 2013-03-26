package core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkResultPrinter {
	private static final String DELIMITER = "\t";
	private static final String TIMINGUNIT = "";
	private BenchmarkRunner runner1;
	private BenchmarkRunner runner2;
	private boolean omitArguments = false;

	private List<BenchmarkRunner> runners;

	public BenchmarkResultPrinter(BenchmarkRunner... runners) {
		if (runners.length < 1) {
			throw new RuntimeException("At least one runner must be added.");
		}
		this.runners = new ArrayList<BenchmarkRunner>();

		for (BenchmarkRunner runner : runners) {
			if (runner != null) {
				this.runners.add(runner);
			}
		}
	}

	public static void printResults(PrintStream out, BenchmarkRunner runner) {
		printResults(out, runner, null);
	}

	public static void printResults(PrintStream out, BenchmarkRunner runner1, BenchmarkRunner runner2) {

		for (int i = 0; i < runner1.getBenchmarks().size(); i++) {
			Benchmark benchmark = runner1.getBenchmarks().get(i);

			StringBuilder line = new StringBuilder();

			line.append('"').append(benchmark.getName()).append(" \"").append(benchmark.getArguments().toString()).append(DELIMITER).append(runner1.getTimes().get(i).toString())
					.append(TIMINGUNIT);

			if (runner2 != null) {
				long relation = Math.round((((double) runner1.getTimes().get(i) / runner2.getTimes().get(i)) - 1) * 100);

				if (relation < 0) {
					relation = -Math.round((((double) runner2.getTimes().get(i) / runner1.getTimes().get(i)) - 1) * 100);
				}

				line.append(DELIMITER).append(runner2.getTimes().get(i).toString()).append(TIMINGUNIT).append(DELIMITER).append(relation + "%");
			}

			line.append('\n');

			out.print(line);
		}

		out.flush();
	}

	public void printResults(PrintStream out) {

		for (int i = 0; i < this.runners.get(0).getBenchmarks().size(); i++) {
			Benchmark benchmark = this.runners.get(0).getBenchmarks().get(i);

			StringBuilder line = new StringBuilder();

			line.append(benchmark.getName()).append(DELIMITER).append(this.printArguments(benchmark));

			double baseValue = this.runners.get(0).getTimes().get(i);

			for (BenchmarkRunner runner : this.runners) {
				double normalizedValue = runner.getTimes().get(i) / baseValue;
				line.append(runner.getTimes().get(i).toString()).append(TIMINGUNIT).append(DELIMITER).append(normalizedValue).append(TIMINGUNIT).append(DELIMITER);
			}

			line.deleteCharAt(line.length() - 1);
			line.append('\n');

			out.print(line);
		}

		out.flush();
	}

	private Object printArguments(Benchmark benchmark) {
		if (this.omitArguments) {
			return "";
		}

		return benchmark.getArguments().toString() + DELIMITER;
	}

	public void setOmitArguments(boolean omitArguments) {
		this.omitArguments = omitArguments;
	}
}