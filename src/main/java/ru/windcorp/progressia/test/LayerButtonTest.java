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

import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.Checkbox;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.RadioButton;
import ru.windcorp.progressia.client.graphics.gui.RadioButtonGroup;
import ru.windcorp.progressia.client.graphics.gui.menu.MenuLayer;
import ru.windcorp.progressia.server.ServerState;

public class LayerButtonTest extends MenuLayer {

	public LayerButtonTest() {
		super("ButtonTest");
		
		addTitle();
		
		Button blockableButton;
		getContent().addChild((blockableButton = new Button("BlockableButton", "Blockable")).addAction(b -> {
			System.out.println("Button Blockable!");
		}));
		blockableButton.setEnabled(false);
		
		getContent().addChild(new Checkbox("EnableButton", "Enable").addAction(b -> {
			blockableButton.setEnabled(((Checkbox) b).isChecked());
		}));
		
		RadioButtonGroup group = new RadioButtonGroup().addAction(g -> {
			System.out.println("RBG! " + g.getSelected().getLabel().getCurrentText());
		});
		
		getContent().addChild(new RadioButton("RB1", "Moon").setGroup(group));
		getContent().addChild(new RadioButton("RB2", "Type").setGroup(group));
		getContent().addChild(new RadioButton("RB3", "Ice").setGroup(group));
		getContent().addChild(new RadioButton("RB4", "Cream").setGroup(group));
		
		getContent().getChild(getContent().getChildren().size() - 1).setEnabled(false);
		
		getContent().addChild(new Label("Hint", new Font().withColor(Colors.LIGHT_GRAY), "This is a MenuLayer"));
		
		getContent().addChild(new Button("Continue", "Continue").addAction(b -> {
			getCloseAction().run();
		}));
		
		getContent().addChild(new Button("Menu", "Menu").addAction(b -> {
			//System.exit(0);
			for (Layer layer : GUI.getLayers())
			{
				GUI.removeLayer(layer);
			}
			GUI.addTopLayer(new LayerTitle("Title"));
			//ClientState.getInstance().;
			
			//ServerState.getInstance().shutdown("Safe Exit");
		}));
		
		getContent().takeFocus();
	}

}
