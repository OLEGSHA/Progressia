/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.client.localization;

import java.lang.ref.WeakReference;
import java.util.*;

import ru.windcorp.progressia.common.util.crash.CrashReports;

public class Localizer {
	private static final Localizer INSTANCE = new Localizer("assets/languages/", "en-US");

	private String language;
	private String fallBackLanguage;
	private final String langFolder;

	private Map<String, String> data;
	private Map<String, String> fallBackData;
	private final Map<String, String> langList;

	private final Collection<WeakReference<LocaleListener>> listeners = Collections
			.synchronizedCollection(new LinkedList<>());

	// lang list must be in the same folder as .lang files
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
			throw CrashReports.report(null, "Language not found: %s", language);
		}
	}

	public synchronized String getFallBackLanguage() {
		return fallBackLanguage;
	}

	public synchronized String getLanguage() {
		return language;
	}

	public synchronized String getValue(String key) {
		if (data == null) {
			throw new IllegalStateException("Localizer not yet initialized");
		}

		if (data.containsKey(key)) {
			return data.get(key);
		} else if (fallBackData.containsKey(key)) {
			return fallBackData.get(key);
		} else {
			return key;
		}
	}

	private void pokeListeners(String newLanguage) {
		// TODO extract as weak bus listener class
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
