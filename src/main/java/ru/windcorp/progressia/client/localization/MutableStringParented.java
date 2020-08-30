package ru.windcorp.progressia.client.localization;

public abstract class MutableStringParented  extends MutableString {
	private final MutableString parent;

	public MutableStringParented(MutableString parent) {
		this.parent = parent;
		listen(parent);
	}

	public MutableString getParent() {
		return parent;
	}
}
