package ru.windcorp.progressia.client.localization;

import java.util.function.Function;

public class MutableStringFunc extends MutableStringParented {
	private final Function<String, String> function;

	public MutableStringFunc(MutableString parent, Function<String, String> f) {
		super(parent);
		this.function = f;
	}

	@Override
	protected String compute() {
		return function.apply(getParent().get());
	}
}
