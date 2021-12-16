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

import java.util.Iterator;

import org.lwjgl.glfw.GLFW;

import ru.windcorp.progressia.client.graphics.flat.AssembledFlatLayer;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.InputBus;
import ru.windcorp.progressia.common.util.StashingStack;

public abstract class GUILayer extends AssembledFlatLayer {

	private final Component root = new Component("Root") {
		@Override
		protected void handleReassemblyRequest() {
			GUILayer.this.invalidate();
		}
	};

	public GUILayer(String name, Layout layout) {
		super(name);
		
		getRoot().setLayout(layout);
		getRoot().addInputListener(KeyEvent.class, this::attemptFocusTransfer, InputBus.Option.IGNORE_FOCUS);
	}

	public Component getRoot() {
		return root;
	}

	@Override
	protected void assemble(RenderTarget target) {
		getRoot().setBounds(0, 0, getWidth(), getHeight());
		getRoot().invalidate();
		getRoot().assemble(target);
	}

	/**
	 * Stack frame for {@link #handleInput(InputEvent)}.
	 */
	private static class EventHandlingFrame {
		Component component;
		Iterator<Component> children;

		void init(Component c) {
			component = c;
			children = c.getChildren().iterator();
		}

		void reset() {
			component = null;
			children = null;
		}
	}

	/**
	 * Stack for {@link #handleInput(InputEvent)}.
	 */
	private StashingStack<EventHandlingFrame> path = new StashingStack<>(64, EventHandlingFrame::new);
	
	/*
	 * This is essentially a depth-first iteration of the component tree. The
	 * recursive procedure has been unrolled to reduce call stack length.
	 */
	@Override
	public void handleInput(InputEvent event) {
		if (!path.isEmpty()) {
			throw new IllegalStateException(
				"path is not empty: " + path + ". Are events being processed concurrently?"
			);
		}

		path.push().init(root);

		while (!path.isEmpty()) {

			Iterator<Component> it = path.peek().children;
			if (it.hasNext()) {

				Component c = it.next();

				if (c.isEnabled()) {
					if (c.getChildren().isEmpty()) {
						c.getInputBus().dispatch(event);
					} else {
						path.push().init(c);
					}
				}

			} else {
				path.peek().component.getInputBus().dispatch(event);
				path.pop().reset();
			}

		}
	}
	
	private void attemptFocusTransfer(KeyEvent e) {
		Component focused = getRoot().findFocused();
		
		if (focused == null) {
			return;
		}
		
		if (e.getKey() == GLFW.GLFW_KEY_TAB && !e.isRelease()) {
			e.consume();
			if (e.hasShift()) {
				focused.focusPrevious();
			} else {
				focused.focusNext();
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		getRoot().invalidate();
	}

}
