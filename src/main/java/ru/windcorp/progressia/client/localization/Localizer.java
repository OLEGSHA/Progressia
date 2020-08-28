package ru.windcorp.progressia.client.localization;

import java.lang.ref.WeakReference;
import java.util.*;

public class Localizer  {
    private static final Localizer instance = new Localizer("assets/languages/lang_list.txt");

    private final Parser langParser;

    private String language;
    private final String langFolder;

    private Map<String, String> data;
    private final Map<String, String> langList;

    private final Collection<WeakReference<LocaleListener>> listeners =
            Collections.synchronizedCollection(new LinkedList<>());

    //lang list must be in the same folder as .lang files
    public Localizer(String langList) {
        this.langFolder = langList.concat("/../");
        langParser = new Parser(langList);
        this.langList = langParser.parse();
    }

    public synchronized void setLanguage(String language) {
        if (langList.containsKey(language)) {
            this.language = language;
            langParser.setFilePath(langFolder + language + ".lang");
            data = langParser.parse();
            pokeListeners(language);
        } else {
            throw new RuntimeException("Language not found: " + language);
        }
    }

    public synchronized String getLanguage() {
        return language;
    }

    public synchronized String getValue(String key) {
        try {
            return data.getOrDefault(key, key);
        } catch (NullPointerException e) {
            e.printStackTrace();
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
        return instance;
    }

    public void addListener(LocaleListener listener) {
        listeners.add(new WeakReference<>(listener));
    }

    public void removeListener(LocaleListener listener) {
        listeners.removeIf(ref -> listener.equals(ref.get()));
    }
}
