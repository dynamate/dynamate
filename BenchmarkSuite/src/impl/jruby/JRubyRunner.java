package impl.jruby;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class JRubyRunner extends BenchmarkRunner {

	public JRubyRunner(String... classpath) {
		super("org.jruby.Main", BenchmarkRunner.WORKSPACE, classpath);
		this.registerBenchmark(new Benchmark("mandelbrot", "benchmark1", "1000"));
		// this.registerBenchmark(new Benchmark("mandelbrot", "benchmark1", "4000"));
		// registerBenchmark(new Benchmark("mandelbrot", "benchmark1", "16000"));
		//
		this.registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", "10"));
		// this.registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", "11"));
		// // registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", "12"));
		//
		this.registerBenchmark(new Benchmark("n-body", "benchmark3", "500000"));
		// this.registerBenchmark(new Benchmark("n-body", "benchmark3", "5000000"));
		// registerBenchmark(new Benchmark("n-body", "benchmark3", "50000000"));
		//
		this.registerBenchmark(new Benchmark("spectral-norm", "benchmark4", "500"));
		// this.registerBenchmark(new Benchmark("spectral-norm", "benchmark4", "3000"));
		// // registerBenchmark(new Benchmark("spectral-norm", "benchmark4", "5500"));
		//
		this.registerBenchmark(new Benchmark("k-nucleotide", "benchmark5", true, "250000"));
		// this.registerBenchmark(new Benchmark("k-nucleotide", "benchmark5", true, "2500000"));
		// // registerBenchmark(new Benchmark("k-nucleotide", "benchmark5", true, "25000000"));
		//
		this.registerBenchmark(new Benchmark("binary-trees", "benchmark6", "12"));
		// this.registerBenchmark(new Benchmark("binary-trees", "benchmark6", "16"));
		// // registerBenchmark(new Benchmark("binary-trees", "benchmark6", "20"));
		//
		this.registerBenchmark(new Benchmark("regex-dna", "benchmark7", true, "50000"));
		// this.registerBenchmark(new Benchmark("regex-dna", "benchmark7", true, "500000"));
		// // registerBenchmark(new Benchmark("regex-dna", "benchmark7", true, "5000000"));
		//
		this.registerBenchmark(new Benchmark("pidigits", "benchmark8", "2000"));
		// this.registerBenchmark(new Benchmark("pidigits", "benchmark8", "6000"));
		// // registerBenchmark(new Benchmark("pidigits", "benchmark8", "10000"));
		//
		this.registerBenchmark(new Benchmark("fasta", "benchmark9", "250000"));
		// this.registerBenchmark(new Benchmark("fasta", "benchmark9", "2500000"));
		// // registerBenchmark(new Benchmark("fasta", "benchmark9", "25000000"));
		//
		this.registerBenchmark(new Benchmark("reverse-complement", "benchmark10", true, "250000"));
		// this.registerBenchmark(new Benchmark("reverse-complement", "benchmark10", true, "2500000"));
		// // registerBenchmark(new Benchmark("reverse-complement", "benchmark10", true, "25000000"));

		this.registerBenchmark(new Benchmark("alternating-dispatches", "benchmark11", "10000000"));
		// this.registerBenchmark(new Benchmark("alternating-dispatches", "benchmark11", "100000000"));
		this.registerBenchmark(new Benchmark("sequential-dispatches", "benchmark12", "10000000"));
		// this.registerBenchmark(new Benchmark("sequential-dispatches", "benchmark12", "100000000"));
	}

	@Override
	protected String getTestFileEnding() {
		return "rb";
	}

	public static void main(String[] args) {
		try {
			JRubyRunner jRubyBenchmark = new JRubyRunner("Dynamate/bin", "Dynamate/classes", "JRubyInvoke/bin", "Dynamate/lib/slf4j-api-1.6.6.jar",
					"Dynamate/lib/slf4j-simple-1.6.6.jar", "jruby/lib/jruby.jar");

			jRubyBenchmark.addSystemProperty("skipIdLibrary", "true");
			jRubyBenchmark.start();

			JRubyRunner jRubyBenchmark2 = new JRubyRunner("Dynamate/bin", "Dynamate/classes", "JRubyInvoke/bin", "Dynamate/lib/slf4j-api-1.6.6.jar",
					"Dynamate/lib/slf4j-simple-1.6.6.jar", "jruby/lib/jruby.jar");

			jRubyBenchmark2.addSystemProperty("skipIdLibrary", "false");
			jRubyBenchmark2.start();

			BenchmarkResultPrinter resultPrinter = new BenchmarkResultPrinter(jRubyBenchmark, jRubyBenchmark2);
			resultPrinter.printResults(System.out);
			// resultPrinter.printResults(new PrintStream(new FileOutputStream("jRuby.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
