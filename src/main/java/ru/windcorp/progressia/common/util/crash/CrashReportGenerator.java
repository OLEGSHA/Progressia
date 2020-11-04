package ru.windcorp.progressia.common.util.crash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CrashReportGenerator {

	private CrashReportGenerator() {
	}

	private static final Path CRASH_REPORTS_PATH = Paths.get("crash-reports");

	private static final Collection<ContextProvider> PROVIDERS = new ArrayList<>();

	private static final Collection<Analyzer> ANALYZERS = new ArrayList<>();

	private static final Logger LOGGER = LogManager.getLogger("crash");

	public static void makeCrashReport(Throwable throwable, String messageFormat, Object... args) {

		StringBuilder output = new StringBuilder();

		for (ContextProvider provider : PROVIDERS) {
			if (provider != null) {
				Map<String, String> buf = new HashMap<>();

				try {
					provider.provideContext(buf);

					addSeparator(output);
					output.append("Provider name: ").append(provider.getName()).append("\n");
					for (Map.Entry<String, String> entry : buf.entrySet()) {
						output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
					}
				} catch (Throwable t) {
					try {
						addSeparator(output);
						output.append(provider.getName()).append(" is broken").append("\n");
					} catch (Throwable th) {
						// You stupid
					}
					// Analyzer is broken
				}
			}
		}

		addSeparator(output);

		boolean analyzerResponseExist = false;
		for (Analyzer analyzer : ANALYZERS) {
			if (analyzer != null) {

				String answer;
				try {
					answer = analyzer.analyze(throwable, messageFormat, args);

					if (answer != null && !answer.isEmpty()) {
						analyzerResponseExist = true;
						output.append(analyzer.getName()).append(": ").append(answer).append("\n");
					}
				} catch (Throwable t) {
					try {
						analyzerResponseExist = true;
						output.append(analyzer.getName()).append(" is broken").append("\n");
					} catch (Throwable th) {
						// You stupid
					}
					// Analyzer is broken
				}
			}
		}

		if (analyzerResponseExist) addSeparator(output);

		// Formatting to a human-readable string
		StringWriter sink = new StringWriter();
		if (throwable != null) {
			try {
				throwable.printStackTrace(new PrintWriter(sink));
			} catch (Exception e) {
				// PLAK
			}
		} else {
			sink.append("Null");
		}

		output.append("Stacktrace: \n");
		output.append(sink.toString()).append("\n");

		LOGGER.fatal("/n" + output.toString());

		try {
			System.err.println(output.toString());
		} catch (Exception e) {
			// PLAK
		}

		generateCrashReportFiles(output.toString());

		System.exit(0);
	}

	public static void generateCrashReportFiles(String output) {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");

		boolean pathExist = false;
		if (!Files.exists(CRASH_REPORTS_PATH)) {

			try {
				Files.createDirectory(CRASH_REPORTS_PATH);
				;
				pathExist = true;
			} catch (IOException e) {
				// Crash Report not created
			}

		} else pathExist = true;

		if (pathExist) {
			createFileForCrashReport(output, CRASH_REPORTS_PATH.toString() + "/latest.log");
			createFileForCrashReport(output, CRASH_REPORTS_PATH.toString() + "/crash-" + dateFormat.format(date) + ".log");
		}
	}

	public static void createFileForCrashReport(String buffer, String filename) {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
			writer.write(buffer);
		} catch (IOException ex) {
			// Crash Report not created
		}
	}

	public static void registerProvider(ContextProvider provider) {
		PROVIDERS.add(provider);
	}

	public static void registerAnalyzer(Analyzer analyzer) {
		ANALYZERS.add(analyzer);
	}

	private static void addSeparator(StringBuilder sb) {
		sb.append("-------------------------------------------------").append("\n");
	}
}
