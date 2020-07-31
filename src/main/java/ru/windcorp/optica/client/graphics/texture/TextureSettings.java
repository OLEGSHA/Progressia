package ru.windcorp.optica.client.graphics.texture;

public class TextureSettings {
	
	private final boolean isFiltered;

	public TextureSettings(boolean isFiltered) {
		this.isFiltered = isFiltered;
	}
	
	public boolean isFiltered() {
		return isFiltered;
	}

}
