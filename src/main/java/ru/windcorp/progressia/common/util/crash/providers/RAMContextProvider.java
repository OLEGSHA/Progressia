package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class RAMContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		output.put("Max Memory", Long.toString(Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
		output.put("Total Memory", Long.toString(Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB");
		output.put("Free Memory", Long.toString(Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB");
		output.put("Used Memory", Long.toString((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " MB");
	}

	@Override
	public String getName() {
		return "RAM Context Provider";
	}
}
