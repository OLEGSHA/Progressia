package ru.windcorp.progressia.common.util.crash;

public interface Analyzer {
	String analyze(Throwable throwable, String messageFormat, Object... args);
}
