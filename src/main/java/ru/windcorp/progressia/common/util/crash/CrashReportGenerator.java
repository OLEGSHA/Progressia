package ru.windcorp.progressia.common.util.crash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class CrashReportGenerator {

	private CrashReportGenerator() {}

	private static final File LATEST_LOG_FILE = new File("crash-reports/latest.log");

	private static final Collection<ContextProvider> PROVIDERS = new ArrayList<>();
	private static final Collection<Map<String, String>> PROVIDER_RESPONSES = new ArrayList<>();

	private static final Collection<Analyzer> ANALYZER = new ArrayList<>();
	private static final Collection<String> ANALYZER_RESPONSES = new ArrayList<>();

	private static final Logger LOGGER = LogManager.getLogger("crash");

	public static void makeCrashReport(Throwable throwable, String messageFormat, Object... args) {

		StringBuilder output = new StringBuilder();

		for (ContextProvider provider : PROVIDERS) {
			if (provider != null) {
				PROVIDER_RESPONSES.add(provider.provideContext());
			}
		}

		for (Analyzer analyzer : ANALYZER) {
			if (analyzer != null) {
				ANALYZER_RESPONSES.add(analyzer.analyze(throwable, messageFormat, args));
			}
		}

		for (Map<String, String> response : PROVIDER_RESPONSES) {
			if (response != null && !response.isEmpty()) {
				addSeparator(output);
				for (Map.Entry<String, String> entry : response.entrySet()) {
					output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
				}
			}
		}

		for (String response : ANALYZER_RESPONSES) {
			if (response != null && !response.isEmpty()) {
				addSeparator(output);
				output.append(response).append("\n");
			}
		}

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

		LOGGER.info(output.toString());
		LOGGER.fatal("Stacktrace: \n" + sink.toString());

		addSeparator(output);

		output.append("Stacktrace: \n");
		output.append(sink.toString()).append("\n");

		try {
			System.err.println(output.toString());
		} catch (Exception e) {
			// PLAK
		}

		createFileForCrashReport(output);
		createFileForLatestCrashReport(output);

		System.exit(0);
	}

	public static void createFileForCrashReport(StringBuilder sb) {
		Date date = new Date();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");

		File logFile = new File("crash-reports/" + "crash-" + dateFormat.format(date) + ".log");

		try (FileOutputStream fos = new FileOutputStream(logFile)) {
			byte[] buffer = sb.toString().getBytes();

			fos.write(buffer, 0, buffer.length);
		} catch (IOException ex) {
			// Crash Report not created
		}
	}

	public static void createFileForLatestCrashReport(StringBuilder sb) {
		try (FileOutputStream fos = new FileOutputStream(LATEST_LOG_FILE)) {
			byte[] buffer = sb.toString().getBytes();

			fos.write(buffer, 0, buffer.length);
		} catch (IOException ex) {
			// Crash Report not created
		}
	}

	public static void registerProvider(ContextProvider provider) {
		PROVIDERS.add(provider);
	}

	public static void registerAnalyzer(Analyzer analyzer) {
		ANALYZER.add(analyzer);
	}

	private static void addSeparator(StringBuilder sb) {
		sb.append("-------------------------------------------------").append("\n");
	}
}
