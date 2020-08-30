package ru.windcorp.progressia.client.localization;

import java.lang.ref.WeakReference;
import java.util.*;

public class Localizer {
	private static final Localizer INSTANCE = new Localizer("assets/languages/", "en-US");

	private String language;
	private String fallBackLanguage;
	private final String langFolder;

	private Map<String, String> data;
	private Map<String, String> fallBackData;
	private final Map<String, String> langList;

	private final Collection<WeakReference<LocaleListener>> listeners =
			Collections.synchronizedCollection(new LinkedList<>());

	//lang list must be in the same folder as .lang files
	public Localizer(String langFolder) {
		this.langFolder = langFolder;
		this.langList = new Parser(langFolder + "lang_list.txt").parse();
	}

	public Localizer(String langFolder, String fallBackLanguage) {
		this(langFolder);
		this.setFallBackLanguage(fallBackLanguage);
	}

	public synchronized void setFallBackLanguage(String language) {
		if (langList.containsKey(language)) {
			this.fallBackLanguage = language;
			fallBackData = new Parser(langFolder + this.fallBackLanguage + ".lang").parse();
		}
	}

	public synchronized void setLanguage(String language) {
		if (langList.containsKey(language)) {
			this.language = language;
			data = new Parser(langFolder + this.language + ".lang").parse();
			pokeListeners(language);
		} else {
			throw new RuntimeException("Language not found: " + language);
		}
	}

	public synchronized String getFallBackLanguage() {
		return fallBackLanguage;
	}

	public synchronized String getLanguage() {
		return language;
	}

	public synchronized String getValue(String key) {
		if (data.containsKey(key)) {
			return data.get(key);
		} else if (fallBackData.containsKey(key)) {
			return fallBackData.get(key);
		} else {
			return key;
		}
	}

	private void pokeListeners(String newLanguage) {
		synchronized (listeners) {
			Iterator<WeakReference<LocaleListener>> iterator = listeners.iterator();
			while (iterator.hasNext()) {
				LocaleListener listenerOrNull = iterator.next().get();
				if (listenerOrNull == null) {
					iterator.remove();
				} else {
					listenerOrNull.onLocaleChanged(newLanguage);
				}
			}
		}
	}

	public static Localizer getInstance() {
		return INSTANCE;
	}

	public void addListener(LocaleListener listener) {
		listeners.add(new WeakReference<>(listener));
	}

	public void removeListener(LocaleListener listener) {
		listeners.removeIf(ref -> listener.equals(ref.get()));
	}
}
