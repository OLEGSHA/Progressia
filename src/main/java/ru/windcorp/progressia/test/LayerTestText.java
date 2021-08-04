package ru.windcorp.progressia.test;

import java.util.function.Supplier;

import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;

public class LayerTestText extends GUILayer {
	public LayerTestText(String name, Supplier<String> value) {
		super(name, new LayoutVertical(20,10));
		
		//MutableString title = new MutableStringLocalized("Layer"+name+".Title");
		Font titleFont = new Font().deriveBold().withColor(Colors.BLACK).withAlign(0.5f);
		getRoot().addChild(new Label(name+".Text", titleFont, value));
	}
	
	public LayerTestText(String name, String value)
	{
		this(name,() -> value);
	}
}
