package ru.windcorp.progressia.client.localization;

public class MutableStringConcat extends MutableString {
	private final Object part0;
	private final Object[] parts;

	public MutableStringConcat(Object object, Object... partsToConcat) {
		this.part0 = object;
		this.parts = partsToConcat;

		listen(object);
		for (Object part : partsToConcat) {
			listen(part);
		}
	}

	@Override
	protected  String compute() {
		StringBuilder sb = new StringBuilder(String.valueOf(part0));
		for  (Object part : parts) {
			sb.append(part);
		}
		return sb.toString();
	}
}
