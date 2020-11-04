package ru.windcorp.progressia.common.util.crash;

import java.util.Map;

public interface ContextProvider {
	void provideContext(Map<String, String> output);
	
	String getName();
}
