package ru.windcorp.progressia.common.util.crash.analyzers;

import ru.windcorp.progressia.common.util.crash.Analyzer;

public class OutOfMemoryAnalyzer implements Analyzer {
	@Override
	public String analyze(Throwable throwable, String messageFormat, Object... args) {
		if (throwable instanceof OutOfMemoryError)
			return "Try to add memory to the JVM";
		return null;
	}

	@Override
	public String getName() {
		return "Out Of Memory Analyzer";
	}
}
