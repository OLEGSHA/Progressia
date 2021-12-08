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

package ru.windcorp.progressia.client.graphics.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.event.ButtonEvent;
import ru.windcorp.progressia.client.graphics.gui.event.EnableEvent;
import ru.windcorp.progressia.client.graphics.gui.event.FocusEvent;
import ru.windcorp.progressia.client.graphics.gui.event.HoverEvent;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;

public abstract class BasicButton extends Component {

	private final Label label;

	private boolean isPressed = false;
	private final Collection<Consumer<BasicButton>> actions = Collections.synchronizedCollection(new ArrayList<>());

	public BasicButton(String name, Label label) {
		super(name);
		this.label = label;

		setLayout(new LayoutAlign(10));
		addChild(this.label);

		setFocusable(true);
		reassembleAt(ARTrigger.HOVER, ARTrigger.FOCUS, ARTrigger.ENABLE);

		// Click triggers
		addInputListener(KeyEvent.class, e -> {
			if (e.isRepeat())
				return;
			
			if (
				e.isLeftMouseButton() ||
					e.getKey() == GLFW.GLFW_KEY_SPACE ||
					e.getKey() == GLFW.GLFW_KEY_ENTER
			) {
				setPressed(e.isPress());
				e.consume();
			}
		});

		addListener(new Object() {

			// Release when losing focus
			@Subscribe
			public void onFocusChange(FocusEvent e) {
				if (!e.getNewState()) {
					setPressed(false);
				}
			}

			// Release when hover ends
			@Subscribe
			public void onHoverEnded(HoverEvent e) {
				if (!e.isNowHovered()) {
					setPressed(false);
				}
			}

			// Release when disabled
			@Subscribe
			public void onDisabled(EnableEvent e) {
				if (!e.getComponent().isEnabled()) {
					setPressed(false);
				}
			}

			// Trigger virtualClick when button is released
			@Subscribe
			public void onRelease(ButtonEvent.Release e) {
				virtualClick();
			}

		});
	}

	public BasicButton(String name, String label, Font labelFont) {
		this(name, new Label(name + ".Label", labelFont, label));
	}

	public BasicButton(String name, String label) {
		this(name, label, new Font());
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void click() {
		setPressed(true);
		setPressed(false);
	}

	public void setPressed(boolean isPressed) {
		if (this.isPressed != isPressed) {
			this.isPressed = isPressed;
			requestReassembly();

			if (isPressed) {
				takeFocus();
			}

			dispatchEvent(ButtonEvent.create(this, this.isPressed));
		}
	}

	public BasicButton addAction(Consumer<BasicButton> action) {
		this.actions.add(Objects.requireNonNull(action, "action"));
		return this;
	}

	public boolean removeAction(Consumer<BasicButton> action) {
		return this.actions.remove(action);
	}

	public void virtualClick() {
		this.actions.forEach(action -> {
			action.accept(this);
		});
	}

	public Label getLabel() {
		return label;
	}

}
