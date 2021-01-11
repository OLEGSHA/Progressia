package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.font.Typeface;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;

public class LayerAbout extends GUILayer {

	public LayerAbout() {
		super("LayerAbout", new LayoutAlign(1, 1, 5));
		
		Panel panel = new Panel("ControlDisplays", new LayoutVertical(5));
		
		Font font = new Font().withColor(Colors.WHITE).deriveOutlined().withAlign(Typeface.ALIGN_RIGHT);
		Font aboutFont = font.withColor(0xFF37A3E6).deriveBold();
		
		panel.addChild(new Label(
				"About", aboutFont,
				new MutableStringLocalized("LayerAbout.Title")
		));
		
		panel.addChild(new Label(
				"Version", font,
				new MutableStringLocalized("LayerAbout.Version").format("pre-TechDemo")
		));
		
		panel.addChild(new Label(
				"DebugHint", font,
				new MutableStringLocalized("LayerAbout.DebugHint")
		));
		
		getRoot().addChild(panel);
		
	}

}
