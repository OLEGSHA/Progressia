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

	private CrashReportGenerator() {
	}

	final static File latestLogFile = new File("crash-reports/latest.log");

	private static Collection<ContextProvider> providers = new ArrayList<ContextProvider>();
	private static Collection<Map<String, String>> providerResponse = new ArrayList<Map<String, String>>();

	private static Collection<Analyzer> analyzers = new ArrayList<Analyzer>();
	private static Collection<String> analyzerResponse = new ArrayList<String>();

	private static final Logger logger = LogManager.getLogger("crash");

	static public void makeCrashReport(Throwable throwable, String messageFormat, Object... args) {

		StringBuilder output = new StringBuilder();

		for (ContextProvider currentProvider : providers) {
			if (currentProvider != null) {
				providerResponse.add(currentProvider.provideContext());
			}
		}

		if (throwable != null) {
			for (Analyzer currentAnalyzer : analyzers) {
				if (currentAnalyzer != null) {
					analyzerResponse.add(currentAnalyzer.getPrompt(throwable, messageFormat, args));
				}
			}
		}

		for (Map<String, String> currentProviderResponse : providerResponse) {
			if (currentProviderResponse != null && !currentProviderResponse.isEmpty()) {
				addSeparator(output);
				for (Map.Entry<String, String> entry : currentProviderResponse.entrySet()) {
					output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
				}
			}
		}

		for (String currentPrompt : analyzerResponse) {
			if (currentPrompt != null && !currentPrompt.isEmpty()) {
				addSeparator(output);
				output.append(currentPrompt).append("\n");
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

		logger.info("\n" + output.toString());
		logger.fatal("Stacktrace: \n" + sink.toString());

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
		try (FileOutputStream fos = new FileOutputStream(latestLogFile)) {
			byte[] buffer = sb.toString().getBytes();

			fos.write(buffer, 0, buffer.length);
		} catch (IOException ex) {
			// Crash Report not created
		}
	}

	public static void registerProvider(ContextProvider provider) {
		providers.add(provider);
	}

	public static void registerAnalyzer(Analyzer analyzer) {
		analyzers.add(analyzer);
	}

	private static void addSeparator(StringBuilder sb) {
		sb.append("-------------------------------------------------").append("\n");
	}
}
