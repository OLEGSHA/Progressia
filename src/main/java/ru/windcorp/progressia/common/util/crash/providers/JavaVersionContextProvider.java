package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class JavaVersionContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		// JAVA
		output.put("Java version", Runtime.version().toString());
		output.put("Java vendor", System.getProperty("java.vendor"));
		output.put("Java home path", System.getProperty("java.home"));
		// VM
		output.put("JVM vendor", System.getProperty("java.vm.vendor"));
		output.put("JVM name", System.getProperty("java.vm.name"));
		output.put("JVM version", System.getProperty("java.vm.version"));
		// Runtime
		output.put("Java Runtime name", System.getProperty("java.runtime.name"));
		output.put("Java Runtime version", System.getProperty("java.runtime.version"));

	}

	@Override
	public String getName() {
		return "Java Version Context Provider";
	}
}
