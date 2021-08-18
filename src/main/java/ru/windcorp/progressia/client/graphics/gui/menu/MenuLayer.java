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
package ru.windcorp.progressia.client.graphics.gui.menu;

import org.lwjgl.glfw.GLFW;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Layout;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;

public class MenuLayer extends GUILayer {

	private final Component content;
	private final Component background;

	private final Runnable closeAction = () -> {
		GUI.removeLayer(this);
	};

	public MenuLayer(String name, Component content) {
		super(name, new LayoutFill(0));

		setCursorPolicy(CursorPolicy.REQUIRE);

		this.background = new Panel(name + ".Background", new LayoutAlign(10), Colors.toVector(0x66000000), null);
		this.content = content;

		background.addChild(content);
		getRoot().addChild(background);
	}

	public MenuLayer(String name, Layout contentLayout) {
		this(name, new Panel(name + ".Content", contentLayout));
	}

	public MenuLayer(String name) {
		this(name, new LayoutVertical(20, 10));
	}

	public Component getContent() {
		return content;
	}

	public Component getBackground() {
		return background;
	}

	protected void addTitle() {
		String translationKey = "Layer" + getName() + ".Title";
		MutableString titleText = new MutableStringLocalized(translationKey);
		Font titleFont = new Font().deriveBold().withColor(Colors.BLACK).withAlign(0.5f);

		Label label = new Label(getName() + ".Title", titleFont, titleText);
		getContent().addChild(label);

		Panel panel = new Panel(getName() + ".Title.Underscore", null, Colors.BLUE, null);
		panel.setLayout(new LayoutFill() {
			@Override
			public Vec2i calculatePreferredSize(Component c) {
				return new Vec2i(label.getPreferredSize().x + 40, 4);
			}
		});
		getContent().addChild(panel);
	}

	protected Runnable getCloseAction() {
		return closeAction;
	}

	@Override
	protected void handleInput(Input input) {

		if (!input.isConsumed()) {
			InputEvent event = input.getEvent();

			if (event instanceof KeyEvent) {
				KeyEvent keyEvent = (KeyEvent) event;
				if (keyEvent.isPress() && keyEvent.getKey() == GLFW.GLFW_KEY_ESCAPE) {
					getCloseAction().run();
				}
			}
		}

		super.handleInput(input);
		input.consume();
	}

}
