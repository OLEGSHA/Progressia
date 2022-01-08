/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package ru.windcorp.progressia.test;

import ru.windcorp.progressia.Progressia;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.font.Typeface;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;

public class LayerAbout extends GUILayer {

	public static String version = "pre-alpha 3";

	public LayerAbout() {
		super("LayerAbout", new LayoutAlign(1, 1, 5));

		Group group = new Group("ControlDisplays", new LayoutVertical(5));

		Font font = new Font().withColor(Colors.WHITE).deriveOutlined().withAlign(Typeface.ALIGN_RIGHT);
		Font aboutFont = font.withColor(0xFF37A3E6).deriveBold();

		group.addChild(
			new Label(
				"About",
				aboutFont,
				new MutableStringLocalized("LayerAbout.Title")
			)
		);

		group.addChild(
			new Label(
				"Version",
				font,
				new MutableStringLocalized("LayerAbout.Version").format(Progressia.getFullerVersion())
			)
		);

		group.addChild(
			new Label(
				"DebugHint",
				font,
				new MutableStringLocalized("LayerAbout.DebugHint")
			)
		);

		getRoot().addChild(group);

	}

}
