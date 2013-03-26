package impl.jython;

import java.io.IOException;

import core.Benchmark;
import core.BenchmarkResultPrinter;
import core.BenchmarkRunner;

public class JythonRunner extends BenchmarkRunner {

	public JythonRunner(String... classpath) {
		super(false, "org.python.util.jython", BenchmarkRunner.WORKSPACE, classpath);

		this.registerBenchmark(new Benchmark("simple", "benchmark_simple", "10000"));
		// this.registerBenchmark(new Benchmark("simple", "benchmark_simple", "100000"));
		// this.registerBenchmark(new Benchmark("simple", "benchmark_simple", "1000000"));

		this.registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", "10"));
		// this.registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", "11"));
		// registerBenchmark(new Benchmark("fannkuch-redux", "benchmark2", "12"));

		this.registerBenchmark(new Benchmark("n-body", "benchmark3", "500000"));
		// this.registerBenchmark(new Benchmark("n-body", "benchmark3", "5000000"));
		// this.registerBenchmark(new Benchmark("n-body", "benchmark3", "50000000"));

		this.registerBenchmark(new Benchmark("sequential dispatch", "benchmark_seq_dispatch", "10000"));
		// this.registerBenchmark(new Benchmark("sequential dispatch", "benchmark_seq_dispatch", "100000"));
		this.registerBenchmark(new Benchmark("alternating dispatch", "benchmark_alt_dispatch", "10000"));
		// this.registerBenchmark(new Benchmark("alternating dispatch", "benchmark_alt_dispatch", "100000"));
	}

	@Override
	protected String getResourcesPath() {
		return "/JythonInvoke/src/test/resources/benchmark";
	}

	@Override
	protected String getTestFileEnding() {
		return "py";
	}

	public static void main(String[] args) {
		try {
			JythonRunner runner1 = new JythonRunner("Dynamate/bin", "Dynamate/classes", "JythonInvoke/bin", "JythonInvoke/lib/jython-dev.jar", "Dynamate/lib/slf4j-api-1.6.6.jar",
					"Dynamate/lib/slf4j-simple-1.6.6.jar", "JythonInvoke/lib/javalib/jffi-ppc-AIX.jar", "JythonInvoke/lib/javalib/servlet-api-2.5.jar",
					"JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/jakarta-oro-2.0.8.jar", "JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/j2ee1.3/servlet.jar",
					"JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/nekohtml.jar", "JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/commons-logging-1.0.4.jar",
					"JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/jdom.jar", "JythonInvoke/lib/javalib/mockrunner-0.4.1/lib/jdk1.5/j2ee1.3/mockrunner-servlet.jar",
					"JythonInvoke/lib/javalib/jffi-i386-SunOS.jar", "JythonInvoke/lib/javalib/jffi-i386-OpenBSD.jar", "JythonInvoke/lib/javalib/asm-4.0_RC2.jar",
					"JythonInvoke/lib/javalib/jffi-Darwin.jar", "JythonInvoke/lib/javalib/jffi-x86_64-Windows.jar", "JythonInvoke/lib/javalib/jffi-i386-FreeBSD.jar",
					"JythonInvoke/lib/javalib/postgresql-8.3-603.jdbc4.jar", "JythonInvoke/lib/javalib/jffi-ppc64-Linux.jar", "JythonInvoke/lib/javalib/jffi-i386-Windows.jar",
					"JythonInvoke/lib/javalib/xercesImpl-2.9.1.jar", "JythonInvoke/lib/javalib/jffi-x86_64-Linux.jar", "JythonInvoke/lib/javalib/antlr-3.1.3.jar",
					"JythonInvoke/lib/javalib/livetribe-jsr223-2.0.5.jar", "JythonInvoke/lib/javalib/jnr-netdb-0.4.jar", "JythonInvoke/lib/javalib/antlr-runtime-3.1.3.jar",
					"JythonInvoke/lib/javalib/jffi-i386-Linux.jar", "JythonInvoke/lib/javalib/jffi-s390x-Linux.jar", "JythonInvoke/lib/javalib/jffi.jar",
					"JythonInvoke/lib/javalib/asm-util-4.0_RC2.jar", "JythonInvoke/lib/javalib/jarjar-0.7.jar", "JythonInvoke/lib/javalib/svnant-jars/svnClientAdapter.jar",
					"JythonInvoke/lib/javalib/svnant-jars/svnjavahl.jar", "JythonInvoke/lib/javalib/svnant-jars/svnant.jar", "JythonInvoke/lib/javalib/libreadline-java-0.8.jar",
					"JythonInvoke/lib/javalib/stringtemplate-3.2.jar", "JythonInvoke/lib/javalib/jffi-ppc-Linux.jar", "JythonInvoke/lib/javalib/mysql-connector-java-5.1.6.jar",
					"JythonInvoke/lib/javalib/jffi-sparc-SunOS.jar", "JythonInvoke/lib/javalib/jffi-sparcv9-SunOS.jar", "JythonInvoke/lib/javalib/jffi-x86_64-SunOS.jar",
					"JythonInvoke/lib/javalib/jffi-x86_64-OpenBSD.jar", "JythonInvoke/lib/javalib/jline-0.9.95-SNAPSHOT.jar", "JythonInvoke/lib/javalib/guava-r07.jar",
					"JythonInvoke/lib/javalib/cpptasks/cpptasks.jar", "JythonInvoke/lib/javalib/profile.jar", "JythonInvoke/lib/javalib/constantine.jar",
					"JythonInvoke/lib/javalib/asm-commons-4.0_RC2.jar", "JythonInvoke/lib/javalib/jaffl.jar", "JythonInvoke/lib/javalib/antlr-2.7.7.jar",
					"JythonInvoke/lib/javalib/jnr-posix.jar", "JythonInvoke/lib/javalib/junit-3.8.2.jar", "JythonInvoke/lib/javalib/jffi-x86_64-FreeBSD.jar");
			runner1.addSystemProperty("skipIdLibrary", "true");
			runner1.start();

			JythonRunner runner2 = new JythonRunner("Dynamate/bin", "Dynamate/classes", "JythonInvoke/bin", "JythonInvoke/lib/jython-dev.jar", "Dynamate/lib/slf4j-api-1.6.6.jar",
					"Dynamate/lib/slf4j-simple-1.6.6.jar", "JythonInvoke/lib/javalib/jffi-ppc-AIX.jar", "JythonInvoke/lib/javalib/servlet-api-2.5.jar",
					"JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/jakarta-oro-2.0.8.jar", "JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/j2ee1.3/servlet.jar",
					"JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/nekohtml.jar", "JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/commons-logging-1.0.4.jar",
					"JythonInvoke/lib/javalib/mockrunner-0.4.1/jar/jdom.jar", "JythonInvoke/lib/javalib/mockrunner-0.4.1/lib/jdk1.5/j2ee1.3/mockrunner-servlet.jar",
					"JythonInvoke/lib/javalib/jffi-i386-SunOS.jar", "JythonInvoke/lib/javalib/jffi-i386-OpenBSD.jar", "JythonInvoke/lib/javalib/asm-4.0_RC2.jar",
					"JythonInvoke/lib/javalib/jffi-Darwin.jar", "JythonInvoke/lib/javalib/jffi-x86_64-Windows.jar", "JythonInvoke/lib/javalib/jffi-i386-FreeBSD.jar",
					"JythonInvoke/lib/javalib/postgresql-8.3-603.jdbc4.jar", "JythonInvoke/lib/javalib/jffi-ppc64-Linux.jar", "JythonInvoke/lib/javalib/jffi-i386-Windows.jar",
					"JythonInvoke/lib/javalib/xercesImpl-2.9.1.jar", "JythonInvoke/lib/javalib/jffi-x86_64-Linux.jar", "JythonInvoke/lib/javalib/antlr-3.1.3.jar",
					"JythonInvoke/lib/javalib/livetribe-jsr223-2.0.5.jar", "JythonInvoke/lib/javalib/jnr-netdb-0.4.jar", "JythonInvoke/lib/javalib/antlr-runtime-3.1.3.jar",
					"JythonInvoke/lib/javalib/jffi-i386-Linux.jar", "JythonInvoke/lib/javalib/jffi-s390x-Linux.jar", "JythonInvoke/lib/javalib/jffi.jar",
					"JythonInvoke/lib/javalib/asm-util-4.0_RC2.jar", "JythonInvoke/lib/javalib/jarjar-0.7.jar", "JythonInvoke/lib/javalib/svnant-jars/svnClientAdapter.jar",
					"JythonInvoke/lib/javalib/svnant-jars/svnjavahl.jar", "JythonInvoke/lib/javalib/svnant-jars/svnant.jar", "JythonInvoke/lib/javalib/libreadline-java-0.8.jar",
					"JythonInvoke/lib/javalib/stringtemplate-3.2.jar", "JythonInvoke/lib/javalib/jffi-ppc-Linux.jar", "JythonInvoke/lib/javalib/mysql-connector-java-5.1.6.jar",
					"JythonInvoke/lib/javalib/jffi-sparc-SunOS.jar", "JythonInvoke/lib/javalib/jffi-sparcv9-SunOS.jar", "JythonInvoke/lib/javalib/jffi-x86_64-SunOS.jar",
					"JythonInvoke/lib/javalib/jffi-x86_64-OpenBSD.jar", "JythonInvoke/lib/javalib/jline-0.9.95-SNAPSHOT.jar", "JythonInvoke/lib/javalib/guava-r07.jar",
					"JythonInvoke/lib/javalib/cpptasks/cpptasks.jar", "JythonInvoke/lib/javalib/profile.jar", "JythonInvoke/lib/javalib/constantine.jar",
					"JythonInvoke/lib/javalib/asm-commons-4.0_RC2.jar", "JythonInvoke/lib/javalib/jaffl.jar", "JythonInvoke/lib/javalib/antlr-2.7.7.jar",
					"JythonInvoke/lib/javalib/jnr-posix.jar", "JythonInvoke/lib/javalib/junit-3.8.2.jar", "JythonInvoke/lib/javalib/jffi-x86_64-FreeBSD.jar");
			runner2.addSystemProperty("skipIdLibrary", "false");
			runner2.start();

			BenchmarkResultPrinter benchmarkResultPrinter = new BenchmarkResultPrinter(runner1, runner2);
			benchmarkResultPrinter.printResults(System.out);
			// benchmarkResultPrinter.printResults(new PrintStream(new FileOutputStream("jython.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
