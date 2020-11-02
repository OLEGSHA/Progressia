package ru.windcorp.progressia.common.util.crash.analyzers;

import ru.windcorp.progressia.common.util.crash.Analyzer;

public class OutOfMemoryAnalyzer implements Analyzer {
	@Override
	public String getPrompt(Throwable throwable, String messageFormat, Object... args) {
		if (throwable instanceof OutOfMemoryError)
			return "Try add memory for the JVM";
		return null;
	}
}
