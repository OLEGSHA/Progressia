package ru.windcorp.progressia.test;

import java.util.function.Consumer;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.localization.MutableString;

public class LayerTestText extends GUILayer {
	
	private final Consumer<LayerTestText> remover;
	
	public LayerTestText(String name, MutableString value, Consumer<LayerTestText> remover) {
		super(name, new LayoutAlign(15));
		this.remover = remover;

		Font titleFont = new Font().deriveBold().withColor(Colors.BLACK).withAlign(0.5f);
		getRoot().addChild(new Label(name + ".Text", titleFont, value));
	}
	
	@Override
	protected void doRender() {
		super.doRender();
		remover.accept(this);
	}
	
}
