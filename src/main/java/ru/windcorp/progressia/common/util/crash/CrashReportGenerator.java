package ru.windcorp.progressia.common.util.crash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.StringBuilderWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CrashReportGenerator {

	private CrashReportGenerator() {}

	private static final Path CRASH_REPORTS_PATH = Paths.get("crash-reports");

	private static final Collection<ContextProvider> PROVIDERS =
			Collections.synchronizedCollection(new ArrayList<>());

	private static final Collection<Analyzer> ANALYZERS =
			Collections.synchronizedCollection(new ArrayList<>());

	private static final Logger LOGGER = LogManager.getLogger("crash");

	/**
	 * <em>This method never returns.</em>
	 * <p>
	 * TODO document
	 * @param throwable
	 * @param messageFormat
	 * @param args
	 */
	public static void crash(Throwable throwable, String messageFormat, Object... args) {
		StringBuilder output = new StringBuilder();

		appendContextProviders(output);
		addSeparator(output);
		if (appendAnalyzers(output, throwable, messageFormat, args)) {
			addSeparator(output);
		}

		appendMessageFormat(output, messageFormat, args);

		appendStackTrace(output, throwable);

		export(output.toString());

		System.exit(0);
	}

	private static void appendContextProviders(StringBuilder output) {
		for (ContextProvider provider : PROVIDERS) {
			if (provider != null) {
				Map<String, String> buf = new HashMap<>();

				try {
					provider.provideContext(buf);

					if (!buf.isEmpty()) {
						addSeparator(output);
						output.append("Provider name: ").append(provider.getName()).append("\n");
						for (Map.Entry<String, String> entry : buf.entrySet()) {
							output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
						}
					}
				} catch (Throwable t) {
					output.append("\n");
					try {
						output.append(provider.getName()).append(" is broken").append("\n");
					} catch (Throwable th) {
						output.append(provider.getClass().getName()).append(" is broken").append("\n");
						// You stupid
					}
					// Analyzer is broken
				}
			}
		}
	}
	
	private static boolean appendAnalyzers(
			StringBuilder output,
			Throwable throwable, String messageFormat, Object[] args
	) {
		boolean analyzerResponsesExist = false;
		for (Analyzer analyzer : ANALYZERS) {
			if (analyzer != null) {

				String answer;
				try {
					answer = analyzer.analyze(throwable, messageFormat, args);

					if (answer != null && !answer.isEmpty()) {
						analyzerResponsesExist = true;
						output.append(analyzer.getName()).append(": ").append(answer).append("\n");
					}
				} catch (Throwable t) {
					try {
						analyzerResponsesExist = true;
						output.append(analyzer.getName()).append(" is broken").append("\n");
					} catch (Throwable th) {
						// You stupid
					}
					// Analyzer is broken
				}
			}
		}

		return analyzerResponsesExist;
	}

	private static void appendMessageFormat(StringBuilder output, String messageFormat, Object... arg) {
		output.append("Provided description: ").append(String.format(messageFormat, arg)).append("\n");

		addSeparator(output);
	}

	private static void appendStackTrace(StringBuilder output, Throwable throwable) {
		output.append("Stacktrace: \n");

		if (throwable == null) {
			output.append("no Throwable provided").append("\n");
			return;
		}

		// Formatting to a human-readable string
		Writer sink = new StringBuilderWriter(output);
		try {
			throwable.printStackTrace(new PrintWriter(sink));
		} catch (Exception e) {
			// PLAK
		}
		output.append("\n");
	}

	private static void export(String report) {
		try {
			LOGGER.fatal("/n" + report);
		} catch (Exception e) {
			// PLAK
		}

		System.err.println(report);

		generateCrashReportFiles(report);
	}

	private static void generateCrashReportFiles(String output) {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");

		try {
			if (!Files.exists(CRASH_REPORTS_PATH)) Files.createDirectory(CRASH_REPORTS_PATH);

			createFileForCrashReport(output, CRASH_REPORTS_PATH.toString() + "/latest.log");
			createFileForCrashReport(output, CRASH_REPORTS_PATH.toString() + "/crash-" + dateFormat.format(date) + ".log");
		} catch (Throwable t) {
			// Crash Report not created
		}
	}

	private static void createFileForCrashReport(String buffer, String filename) {
		try (
				BufferedWriter writer = Files.newBufferedWriter(
						Paths.get(filename),
						StandardCharsets.UTF_8
				)
		) {
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
		sb.append(
				// 80 chars
				"--------------------------------------------------------------------------------"
		).append("\n");
	}
}
