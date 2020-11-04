package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class OSContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		output.put("Name OS", System.getProperty("os.name"));
		output.put("Version OS", System.getProperty("os.version"));
		output.put("Architecture OS", System.getProperty("os.arch"));
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
