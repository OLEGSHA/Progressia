package ru.windcorp.progressia.common.util.crash;

/**
 * A crash report utility that performs analysis of a problem during crash
 * report generation and presents its conclusion to the user via the crash report.
 * Unlike {@link ContextProvider}s, Analyzers are provided with the reported problem
 * details.
 * @see ContextProvider
 * @author serega404
 */
public interface Analyzer {
	
	/**
	 * Provides a human-readable string describing this analyzer's conclusion
	 * on the presented problem, or returns {@code null} if no conclusion
	 * could be made.
	 * @param throwable The reported throwable (may be {@code null})
	 * @param messageFormat A {@linkplain java.util.Formatter#syntax format string} of a
	 * human-readable description of the problem 
	 * @param args The arguments for the format string
	 * @return a conclusion or {@code null}
	 */
	String analyze(Throwable throwable, String messageFormat, Object... args);

	/**
	 * Returns this analyzer's human-readable name.
	 * It should be A String In Title Case With Spaces.
	 * @return this analyzer's name
	 */
	String getName();
	
}
