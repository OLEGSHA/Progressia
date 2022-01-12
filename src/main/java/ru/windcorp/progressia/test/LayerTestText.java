package ru.windcorp.progressia.test;

import java.util.function.Consumer;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.ColorScheme;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.localization.MutableString;

public class LayerTestText extends GUILayer {
	
	private final Consumer<LayerTestText> remover;
	
	public LayerTestText(String name, MutableString value, Consumer<LayerTestText> remover) {
		super(name, new LayoutFill());
		this.remover = remover;

		Panel panel = new Panel(name + ".Background", new LayoutAlign(15), ColorScheme.get("Core:Background"), null);
		Font titleFont = new Font().deriveBold().withColor(ColorScheme.get("Core:Text")).withAlign(0.5f);
		panel.addChild(new Label(name + ".Text", titleFont, value));
		getRoot().addChild(panel);
	}
	
	@Override
	protected void doRender() {
		super.doRender();
		remover.accept(this);
	}
	
}
