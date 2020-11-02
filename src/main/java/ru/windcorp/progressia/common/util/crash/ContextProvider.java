package ru.windcorp.progressia.common.util.crash;

import java.util.Map;

public interface ContextProvider {

	Map<String, String> provideContext();
}
