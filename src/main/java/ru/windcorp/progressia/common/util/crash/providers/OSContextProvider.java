package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class OSContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		output.put("OS Name", System.getProperty("os.name"));
		output.put("OS Version", System.getProperty("os.version"));
		output.put("OS Architecture", System.getProperty("os.arch"));
	}

	@Override
	public String getName() {
		return "OS Context Provider";
	}
}
