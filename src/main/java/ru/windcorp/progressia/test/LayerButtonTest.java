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

import org.lwjgl.glfw.GLFW;

import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.Checkbox;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.RadioButton;
import ru.windcorp.progressia.client.graphics.gui.RadioButtonGroup;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderHorizontal;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;

public class LayerButtonTest extends GUILayer {

	public LayerButtonTest() {
		super("LayerButtonTest", new LayoutBorderHorizontal(0));
		
		Group background = new Group("Background", new LayoutAlign(10)) {
			@Override
			protected void assembleSelf(RenderTarget target) {
				target.fill(Colors.toVector(0x88FFFFFF));
			}
		};
		
		Panel panel = new Panel("Panel", new LayoutVertical(10));
		
		Button blockableButton;
		panel.addChild((blockableButton = new Button("BlockableButton", "Blockable")).addAction(b -> {
			System.out.println("Button Blockable!");
		}));
		blockableButton.setEnabled(false);
		
		panel.addChild(new Checkbox("EnableButton", "Enable").addAction(b -> {
			blockableButton.setEnabled(((Checkbox) b).isChecked());
		}));
		
		RadioButtonGroup group = new RadioButtonGroup().addAction(g -> {
			System.out.println("RBG! " + g.getSelected().getLabel().getCurrentText());
		});
		
		panel.addChild(new RadioButton("RB1", "Moon").setGroup(group));
		panel.addChild(new RadioButton("RB2", "Type").setGroup(group));
		panel.addChild(new RadioButton("RB3", "Ice").setGroup(group));
		panel.addChild(new RadioButton("RB4", "Cream").setGroup(group));
		
		panel.getChild(panel.getChildren().size() - 1).setEnabled(false);
		
		panel.getChild(1).takeFocus();
		
		background.addChild(panel);
		getRoot().addChild(background.setLayoutHint(LayoutBorderHorizontal.CENTER));
	}
	
	@Override
	protected void handleInput(Input input) {
		
		if (!input.isConsumed()) {
			
			InputEvent e = input.getEvent();
			
			if ((e instanceof KeyEvent) && ((KeyEvent) e).isPress() && ((KeyEvent) e).getKey() == GLFW.GLFW_KEY_ESCAPE) {
				GUI.removeLayer(this);
				GLFW.glfwSetInputMode(GraphicsBackend.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			}
			
		}
		
		super.handleInput(input);
		input.consume();
	}

}
