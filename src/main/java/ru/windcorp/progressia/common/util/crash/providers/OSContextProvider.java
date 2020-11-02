package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.HashMap;
import java.util.Map;

public class OSContextProvider implements ContextProvider {

	@Override
	public Map<String, String> provideContext() {
		Map<String, String> result = new HashMap<>();
		result.put("Name OS", System.getProperty("os.name"));
		result.put("Version OS", System.getProperty("os.version"));
		result.put("Architecture OS", System.getProperty("os.arch"));
		return result;
	}
}
