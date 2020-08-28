package ru.windcorp.progressia.client.localization;

@FunctionalInterface
public interface LocaleListener {
    void onLocaleChanged(String newLanguage);
}