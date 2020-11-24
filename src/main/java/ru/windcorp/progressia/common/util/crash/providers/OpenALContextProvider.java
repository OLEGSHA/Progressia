package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.client.audio.AudioManager;
import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class OpenALContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		output.put("OpenAL version", AudioManager.ALversion());
	}

	@Override
	public String getName() {
		return "Audio Context Provider";
	}
}
