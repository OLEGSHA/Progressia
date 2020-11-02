package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.HashMap;
import java.util.Map;

public class OSContextProvider implements ContextProvider {

	@Override
	public Map<String, String> provideContext() {
		Map<String, String> theThings = new HashMap<>();
		theThings.put("Name OS", System.getProperty("os.name"));
		theThings.put("Version OS", System.getProperty("os.version"));
		theThings.put("Architecture OS", System.getProperty("os.arch"));
		return theThings;
	}
}
