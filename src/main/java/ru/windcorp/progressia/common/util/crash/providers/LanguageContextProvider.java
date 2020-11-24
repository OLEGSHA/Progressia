package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class LanguageContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		output.put("Language", Localizer.getInstance().getLanguage());
	}

	@Override
	public String getName() {
		return "Language Context Provider";
	}
}
