package ru.windcorp.progressia.client.localization;

import java.util.IllegalFormatException;

public class MutableStringFormatter extends MutableString {
	private final Object format;
	private final Object[] args;

	public MutableStringFormatter(Object format, Object[] args) {
		this.format = format;
		this.args = args;
		listen(format);

		for (Object arg : args) {
			listen(arg);
		}
	}

	@Override
	protected String compute() {
		String format = this.format.toString();
		try {
			return String.format(format, this.args);
		} catch (IllegalFormatException e) {
			return format;
		}
	}

}
