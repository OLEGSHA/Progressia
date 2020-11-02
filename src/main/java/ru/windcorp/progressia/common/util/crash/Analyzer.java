package ru.windcorp.progressia.common.util.crash;

public interface Analyzer {
	String getPrompt(Throwable throwable, String messageFormat, Object... args);
}
