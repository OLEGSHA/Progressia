package ru.windcorp.progressia.client.localization;

public class MutableStringLocalized extends MutableString {
	private final String key;

	LocaleListener listener = l -> update();

	public MutableStringLocalized(String key) {
		this.key = key;
		Localizer.getInstance().addListener(listener);
		update();
	}

	@Override
	protected String compute() {
		return Localizer.getInstance().getValue(key);
	}
}
