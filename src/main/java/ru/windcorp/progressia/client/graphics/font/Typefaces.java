package ru.windcorp.progressia.client.graphics.font;

public class Typefaces {
	
	private static Typeface def = null;
	
	public static Typeface getDefault() {
		return def;
	}
	
	public static void setDefault(Typeface def) {
		Typefaces.def = def;
	}

}
