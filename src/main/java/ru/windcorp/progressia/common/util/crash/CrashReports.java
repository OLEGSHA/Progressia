/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.common.util.crash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.StringBuilderWriter;

import ru.windcorp.jputil.chars.StringUtil;

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

/**
 * A utility for reporting critical problems, gathering system context and
 * terminating the application consequently (crashing). Do not hesitate to use
 * {@link #report(Throwable, String, Object...)} at every other line.
 * 
 * @author serega404
 */
public class CrashReports {

	private static final Path CRASH_REPORTS_PATH = Paths.get("crash-reports");

	private static final Collection<ContextProvider> PROVIDERS = Collections.synchronizedCollection(new ArrayList<>());

	private static final Collection<Analyzer> ANALYZERS = Collections.synchronizedCollection(new ArrayList<>());

	private static final Logger LOGGER = LogManager.getLogger("crash");

	/**
	 * Creates a {@link ReportedException} that describes the provided problem
	 * so the program can crash later. This method is intended to be used like
	 * so:
	 * 
	 * <pre>
	 * try {
	 * 	doSomethingDifficult(x);
	 * } catch (CouldntMakeItException e) {
	 * 	throw CrashReports.report(e, "We couldn't make it at x = %d", x);
	 * }
	 * </pre>
	 * <p>
	 * Such usage ensures that the report will be dealt with at the top of the
	 * call stack (at least in methods that have a properly set up
	 * {@linkplain #crash(Throwable, String, Object...) crash handler}). Not
	 * throwing the returned exception is pointless; using this in a thread
	 * without a crash handler will not produce a crash.
	 * <p>
	 * Avoid inserting variable information into {@code messageFormat} directly;
	 * use {@linkplain Formatter#summary format string} syntax and {@code args}.
	 * Different Strings in {@code messageFormat} may be interpreted as
	 * unrelated problems by {@linkplain Analyzer crash analyzers}.
	 * 
	 * @param throwable
	 *            a {@link Throwable} that caused the problem, if any;
	 *            {@code null} otherwise
	 * @param messageFormat
	 *            a human-readable description of the problem displayed in the
	 *            crash report
	 * @param args
	 *            an array of arguments for formatting {@code messageFormat}
	 * @return an exception containing the provided information that must be
	 *         thrown
	 */
	public static ReportedException report(Throwable throwable, String messageFormat, Object... args) {
		if (throwable instanceof ReportedException)
			return (ReportedException) throwable;

		return new ReportedException(throwable, messageFormat, args);
	}

	/**
	 * Crashes the program due to the supplied problem.
	 * <p>
	 * <em>Use {@link #report(Throwable, String, Object...)} unless you are
	 * creating a catch-all handler for a thread.</em>
	 * <p>
	 * This method recovers information about the problem by casting
	 * {@code throwable} to {@link ReportedException}, or, failing that, uses
	 * the provided arguments as the information instead. It then constructs a
	 * full crash report, exports it and terminates the program by invoking
	 * {@link System#exit(int)}.
	 * <p>
	 * Such behavior can be dangerous or lead to unwanted consequences in the
	 * middle of the call stack, so it is necessary to invoke this method as
	 * high on the call stack as possible, usually in a {@code catch} clause of
	 * a {@code try} statement enveloping the thread's main method(s).
	 * 
	 * @param throwable
	 *            a {@link ReportedException} or another {@link Throwable} that
	 *            caused the problem, if any; {@code null} otherwise
	 * @param messageFormat
	 *            a human-readable description of the problem used when
	 *            {@code throwable} is not a {@link ReportedException}. See
	 *            {@link #report(Throwable, String, Object...)} for details.
	 * @param args
	 *            an array of arguments for formatting {@code messageFormat}
	 * @return {@code null}, although this method never returns normally.
	 *         Provided for convenience.
	 */
	public static RuntimeException crash(Throwable throwable, String messageFormat, Object... args) {
		final StackTraceElement[] reportStackTrace;

		if (throwable instanceof ReportedException) {
			ReportedException reportedException = (ReportedException) throwable;

			// Discard provided arguments
			throwable = reportedException.getCause();
			messageFormat = reportedException.getMessageFormat();
			args = reportedException.getArgs();

			reportStackTrace = reportedException.getStackTrace();
		} else {
			reportStackTrace = getCurrentStackTrace();
		}

		StringBuilder output = new StringBuilder();

		try {
			String.format(messageFormat, args);
		} catch (IllegalFormatException e) {
			messageFormat = StringUtil.replaceAll(messageFormat, "%", "%%");

			if (args.length != 0) {
				messageFormat += "\nArgs:";
				for (Object arg : args) {
					try {
						messageFormat += " \"" + arg.toString() + "\"";
					} catch (Throwable t) {
						messageFormat += " exc: \"" + t.getClass().toString() + "\"";
					}
				}
				args = new Object[0]; // clear args
			}

			messageFormat += "\nCould not format provided description";
		}

		appendContextProviders(output);
		addSeparator(output);
		if (appendAnalyzers(output, throwable, messageFormat, args)) {
			addSeparator(output);
		}

		appendMessageFormat(output, messageFormat, args);

		appendStackTrace(output, reportStackTrace, "Reported at:");
		output.append('\n');
		appendThrowable(output, throwable);

		export(output.toString());

		System.exit(0);
		return null;
	}

	private static StackTraceElement[] getCurrentStackTrace() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		final int trim = 3;

		return Arrays.copyOfRange(stackTrace, trim, stackTrace.length);
	}

	private static void appendContextProviders(StringBuilder output) {

		// Do a local copy to avoid deadlocks -OLEGSHA
		ContextProvider[] localProvidersCopy = PROVIDERS.toArray(new ContextProvider[PROVIDERS.size()]);

		for (ContextProvider provider : localProvidersCopy) {
			if (provider == null)
				continue;

			try {
				Map<String, String> buf = new HashMap<>();
				provider.provideContext(buf);

				if (!buf.isEmpty()) {
					addSeparator(output);
					output.append(StringUtil.center(provider.getName(), 80)).append("\n");
					for (Map.Entry<String, String> entry : buf.entrySet()) {
						output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
					}
				}
			} catch (Throwable t) {
				String providerName;

				try {
					providerName = provider.getName();
				} catch (Throwable t1) {
					providerName = provider.getClass().getName();
				}

				output.append(providerName).append(" is broken").append("\n");
				// ContextProvider is broken
			}
		}
	}

	private static boolean appendAnalyzers(StringBuilder output, Throwable throwable, String messageFormat,
			Object[] args) {
		boolean analyzerResponsesExist = false;

		// Do a local copy to avoid deadlocks -OLEGSHA
		Analyzer[] localAnalyzersCopy = ANALYZERS.toArray(new Analyzer[ANALYZERS.size()]);

		if (localAnalyzersCopy.length > 0) {
			output.append(StringUtil.center("Analyzers", 80)).append("\n");
		}

		for (Analyzer analyzer : localAnalyzersCopy) {
			if (analyzer == null)
				continue;

			String answer;
			try {
				answer = analyzer.analyze(throwable, messageFormat, args);

				if (answer != null && !answer.isEmpty()) {
					analyzerResponsesExist = true;
					output.append(analyzer.getName()).append(": ").append(answer).append("\n");
				}
			} catch (Throwable t) {
				analyzerResponsesExist = true;

				output.append("\n");

				String analyzerName;

				try {
					analyzerName = analyzer.getName();
				} catch (Throwable t1) {
					analyzerName = analyzer.getClass().getName();
				}

				output.append(analyzerName).append(" is broken").append("\n");
				// Analyzer is broken
			}
		}

		return analyzerResponsesExist;
	}

	private static void appendMessageFormat(StringBuilder output, String messageFormat, Object... arg) {
		output.append("Provided description: \n");
		if (messageFormat.isEmpty())
			output.append("none").append("\n");
		else
			output.append(String.format(messageFormat, arg)).append("\n");

		addSeparator(output);
	}

	private static void appendThrowable(StringBuilder output, Throwable throwable) {
		if (throwable == null) {
			output.append("No Throwable provided").append("\n");
			return;
		}

		output.append("Reported Throwable:\n");

		// Formatting to a human-readable string
		Writer sink = new StringBuilderWriter(output);
		try {
			throwable.printStackTrace(new PrintWriter(sink));
		} catch (Exception e) {
			// PLAK
		}
		output.append('\n');
	}

	private static void appendStackTrace(StringBuilder output, StackTraceElement[] stackTrace, String header) {
		output.append(header).append('\n');

		for (StackTraceElement element : stackTrace) {
			output.append("\tat ").append(element).append('\n');
		}
	}

	private static void export(String report) {
		try {
			LOGGER.fatal("\n" + report);
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
			if (!Files.exists(CRASH_REPORTS_PATH))
				Files.createDirectory(CRASH_REPORTS_PATH);

			createFileForCrashReport(output, CRASH_REPORTS_PATH.toString() + "/latest.log");
			createFileForCrashReport(output,
					CRASH_REPORTS_PATH.toString() + "/crash-" + dateFormat.format(date) + ".log");
		} catch (Throwable t) {
			// Crash Report not created
		}
	}

	private static void createFileForCrashReport(String buffer, String filename) {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
			writer.write(buffer);
		} catch (IOException ex) {
			// Crash Report not created
		}
	}

	private static void addSeparator(StringBuilder sb) {
		sb.append(StringUtil.sequence('-', 80)).append("\n");
	}

	/**
	 * Registers the provided {@link ContextProvider} so it is consulted in the
	 * case of a crash.
	 * 
	 * @param provider
	 *            the provider to register
	 */
	public static void registerProvider(ContextProvider provider) {
		PROVIDERS.add(provider);
	}

	/**
	 * Registers the provided {@link Analyzer} so it is consulted in the case of
	 * a crash.
	 * 
	 * @param analyzer
	 *            the analyzer to register
	 */
	public static void registerAnalyzer(Analyzer analyzer) {
		ANALYZERS.add(analyzer);
	}

	/**
	 * A wrapper used by {@link CrashReports} to transfer problem details from
	 * the place of occurrence to the handler at the top of the stack. Rethrow
	 * if caught (unless using
	 * {@link CrashReports#report(Throwable, String, Object...)}, which does so
	 * automatically).
	 * 
	 * @author serega404
	 */
	public static class ReportedException extends RuntimeException {

		private static final long serialVersionUID = 223720835231091533L;

		private final String messageFormat;
		private final Object[] args;

		/**
		 * Constructs a {@link ReportedException}.
		 * 
		 * @param throwable
		 *            the reported {@link Throwable} or {@code null}
		 * @param messageFormat
		 *            the reported message format. <em>This is not the message
		 *            of the constructed Exception</em>.
		 * @param args
		 *            the reported message format arguments
		 */
		public ReportedException(Throwable throwable, String messageFormat, Object... args) {
			super(throwable);
			this.messageFormat = messageFormat;
			this.args = args;
		}

		/**
		 * Returns the reported message format.
		 * 
		 * @return message format
		 */
		public String getMessageFormat() {
			return messageFormat;
		}

		/**
		 * Returns the reported message format arguments.
		 * 
		 * @return message format arguments
		 */
		public Object[] getArgs() {
			return args;
		}
	}

	private CrashReports() {

	}

}
