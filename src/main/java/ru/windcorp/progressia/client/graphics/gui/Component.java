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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.EventBus;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.event.ChildAddedEvent;
import ru.windcorp.progressia.client.graphics.gui.event.ChildRemovedEvent;
import ru.windcorp.progressia.client.graphics.gui.event.FocusEvent;
import ru.windcorp.progressia.client.graphics.gui.event.HoverEvent;
import ru.windcorp.progressia.client.graphics.gui.event.ParentChangedEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.client.graphics.input.bus.InputBus;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;
import ru.windcorp.progressia.common.util.Named;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.crash.ReportingEventBus;

public class Component extends Named {

	private final List<Component> children = Collections.synchronizedList(new CopyOnWriteArrayList<>());

	private Component parent = null;

	private EventBus eventBus = null;
	private InputBus inputBus = null;

	private int x, y;
	private int width, height;

	private boolean valid = false;

	private Vec2i preferredSize = null;

	private Object layoutHint = null;
	private Layout layout = null;

	private boolean isFocusable = false;
	private boolean isFocused = false;

	private boolean isHovered = false;

	public Component(String name) {
		super(name);
	}

	public Component getParent() {
		return parent;
	}

	protected void setParent(Component parent) {
		if (this.parent != parent) {
			Component previousParent = this.parent;
			this.parent = parent;

			dispatchEvent(new ParentChangedEvent(this, previousParent, parent));
		}
	}

	public List<Component> getChildren() {
		return children;
	}

	public Component getChild(int index) {
		synchronized (getChildren()) {
			if (index < 0 || index >= getChildren().size())
				return null;
			return getChildren().get(index);
		}
	}

	public int getChildIndex(Component child) {
		return getChildren().indexOf(child);
	}

	public int getOwnIndex() {
		Component parent = getParent();
		if (parent != null) {
			return parent.getChildIndex(this);
		}

		return -1;
	}

	public void moveChild(Component child, int newIndex) {
		if (newIndex == -1)
			newIndex = getChildren().size() - 1;

		if (getChildren().remove(child)) {
			getChildren().add(newIndex, child);
			invalidate();
		}
	}

	public void moveSelf(int newIndex) {
		Component parent = getParent();
		if (parent != null) {
			parent.moveChild(this, newIndex);
		}
	}

	public Component addChild(Component child, int index) {
		if (index == -1)
			index = getChildren().size();

		invalidate();
		getChildren().add(index, child);
		child.setParent(this);

		dispatchEvent(new ChildAddedEvent(this, child));

		return this;
	}

	public Component addChild(Component child) {
		return addChild(child, -1);
	}

	public Component removeChild(Component child) {
		if (!getChildren().contains(child)) {
			return this;
		}

		if (child.isFocused()) {
			child.focusNext();
		}

		invalidate();
		getChildren().remove(child);
		child.setParent(null);

		dispatchEvent(new ChildRemovedEvent(this, child));

		return this;
	}

	public synchronized int getX() {
		return x;
	}

	public synchronized int getY() {
		return y;
	}

	public synchronized Component setPosition(int x, int y) {
		invalidate();
		this.x = x;
		this.y = y;
		return this;
	}

	public synchronized int getWidth() {
		return width;
	}

	public synchronized int getHeight() {
		return height;
	}

	public synchronized Component setSize(int width, int height) {
		invalidate();
		this.width = width;
		this.height = height;
		return this;
	}

	public Component setSize(Vec2i size) {
		return setSize(size.x, size.y);
	}

	public synchronized Component setBounds(int x, int y, int width, int height) {
		setPosition(x, y);
		setSize(width, height);
		return this;
	}

	public Component setBounds(int x, int y, Vec2i size) {
		return setBounds(x, y, size.x, size.y);
	}

	public boolean isValid() {
		return valid;
	}

	public synchronized void invalidate() {
		valid = false;
		getChildren().forEach(child -> child.invalidate());
	}

	public synchronized void validate() {
		Component parent = getParent();
		invalidate();

		if (parent == null) {
			layoutSelf();
		} else {
			parent.validate();
		}
	}

	protected synchronized void layoutSelf() {
		try {
			if (getLayout() != null) {
				getLayout().layout(this);
			}

			getChildren().forEach(child -> {
				child.layoutSelf();
			});

			valid = true;
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not layout Component %s", this);
		}
	}

	public synchronized Vec2i getPreferredSize() {
		if (preferredSize != null) {
			return preferredSize;
		}

		if (getLayout() != null) {
			try {
				return getLayout().calculatePreferredSize(this);
			} catch (Exception e) {
				throw CrashReports.report(e, "Could not calculate preferred size for Component %s", this);
			}
		}

		return new Vec2i(0, 0);
	}

	public synchronized Component setPreferredSize(Vec2i preferredSize) {
		this.preferredSize = preferredSize;
		return this;
	}

	public Component setPreferredSize(int width, int height) {
		return setPreferredSize(new Vec2i(width, height));
	}

	public Layout getLayout() {
		return layout;
	}

	public synchronized Component setLayout(Layout layout) {
		invalidate();
		this.layout = layout;
		return this;
	}

	public Object getLayoutHint() {
		return layoutHint;
	}

	public Component setLayoutHint(Object hint) {
		this.layoutHint = hint;
		return this;
	}

	public boolean isFocusable() {
		return isFocusable;
	}

	public Component setFocusable(boolean focusable) {
		this.isFocusable = focusable;
		return this;
	}

	public boolean isFocused() {
		return isFocused;
	}

	protected synchronized void setFocused(boolean focus) {
		if (focus != this.isFocused) {
			dispatchEvent(new FocusEvent(this, focus));
			this.isFocused = focus;
		}
	}

	public Component takeFocus() {
		if (isFocused()) {
			return this;
		}

		Component comp = this;
		Component focused = null;

		while (comp != null) {
			if ((focused = comp.findFocused()) != null) {
				focused.setFocused(false);
				setFocused(true);
				return this;
			}

			comp = comp.getParent();
		}

		setFocused(true);
		return this;
	}

	public void focusNext() {
		Component component = this;

		while (true) {

			component = component.getNextFocusCandidate(true);
			if (component == this) {
				return;
			}

			if (component.isFocusable()) {
				setFocused(false);
				component.setFocused(true);
				return;
			}

		}
	}

	private Component getNextFocusCandidate(boolean canUseChildren) {
		if (canUseChildren)
			synchronized (getChildren()) {
				if (!getChildren().isEmpty()) {
					return getChild(0);
				}
			}

		Component parent = getParent();
		if (parent != null) {
			synchronized (parent.getChildren()) {
				int ownIndex = parent.getChildIndex(this);
				if (ownIndex != parent.getChildren().size() - 1) {
					return parent.getChild(ownIndex + 1);
				}
			}

			return parent.getNextFocusCandidate(false);
		}

		return this;
	}

	public void focusPrevious() {
		Component component = this;

		while (true) {

			component = component.getPreviousFocusCandidate();
			if (component == this) {
				return;
			}

			if (component.isFocusable()) {
				setFocused(false);
				component.setFocused(true);
				return;
			}

		}
	}

	private Component getPreviousFocusCandidate() {
		Component parent = getParent();
		if (parent != null) {
			synchronized (parent.getChildren()) {
				int ownIndex = parent.getChildIndex(this);
				if (ownIndex != 0) {
					return parent.getChild(ownIndex - 1).getLastDeepChild();
				}
			}

			return parent;
		}

		return getLastDeepChild();
	}

	private Component getLastDeepChild() {
		synchronized (getChildren()) {
			if (!getChildren().isEmpty()) {
				return getChild(getChildren().size() - 1).getLastDeepChild();
			}

			return this;
		}
	}

	public synchronized Component findFocused() {
		if (isFocused()) {
			return this;
		}

		Component result;

		synchronized (getChildren()) {
			for (Component c : getChildren()) {
				result = c.findFocused();
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	public boolean isHovered() {
		return isHovered;
	}

	protected void setHovered(boolean isHovered) {
		if (this.isHovered != isHovered) {
			this.isHovered = isHovered;

			if (!isHovered && !getChildren().isEmpty()) {

				getChildren().forEach(child -> {
					if (child.isHovered()) {
						child.setHovered(false);
						return;
					}
				});
			}

			dispatchEvent(new HoverEvent(this, isHovered));
		}
	}

	public void addListener(Object listener) {
		if (eventBus == null) {
			eventBus = ReportingEventBus.create(getName());
		}

		eventBus.register(listener);
	}

	public void removeListener(Object listener) {
		if (eventBus == null)
			return;
		eventBus.unregister(listener);
	}

	public void dispatchEvent(Object event) {
		if (eventBus == null)
			return;
		eventBus.post(event);
	}

	public <T extends InputEvent> void addListener(Class<? extends T> type, boolean handlesConsumed,
			InputListener<T> listener) {
		if (inputBus == null) {
			inputBus = new InputBus();
		}

		inputBus.register(type, handlesConsumed, listener);
	}

	public <T extends InputEvent> void addListener(Class<? extends T> type, InputListener<T> listener) {
		if (inputBus == null) {
			inputBus = new InputBus();
		}

		inputBus.register(type, listener);
	}

	public void removeListener(InputListener<?> listener) {
		if (inputBus != null) {
			inputBus.unregister(listener);
		}
	}

	protected void handleInput(Input input) {
		if (inputBus != null) {
			inputBus.dispatch(input);
		}
	}

	public void dispatchInput(Input input) {
		try {
			switch (input.getTarget()) {
			case FOCUSED:
				dispatchInputToFocused(input);
				break;
			case HOVERED:
				dispatchInputToHovered(input);
				break;
			case ALL:
			default:
				dispatchInputToAll(input);
				break;
			}
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not dispatch input to Component %s", this);
		}
	}

	private void dispatchInputToFocused(Input input) {
		Component c = findFocused();

		if (c == null)
			return;
		if (attemptFocusTransfer(input, c))
			return;

		while (c != null) {
			c.handleInput(input);
			c = c.getParent();
		}
	}

	private void dispatchInputToHovered(Input input) {
		getChildren().forEach(child -> {
			if (child.containsCursor()) {
				child.setHovered(true);

				if (!input.isConsumed()) {
					child.dispatchInput(input);
				}
			} else {
				child.setHovered(false);
			}
		});

		handleInput(input);
	}

	private void dispatchInputToAll(Input input) {
		getChildren().forEach(c -> c.dispatchInput(input));
		handleInput(input);
	}

	private boolean attemptFocusTransfer(Input input, Component focused) {
		if (input.isConsumed())
			return false;
		if (!(input.getEvent() instanceof KeyEvent))
			return false;

		KeyEvent keyInput = (KeyEvent) input.getEvent();

		if (keyInput.getKey() == GLFW.GLFW_KEY_TAB && !keyInput.isRelease()) {
			input.consume();
			if (keyInput.hasShift()) {
				focused.focusPrevious();
			} else {
				focused.focusNext();
			}
			return true;
		}

		return false;
	}

	public synchronized boolean contains(int x, int y) {
		return x >= getX() && x < getX() + getWidth() && y >= getY() && y < getY() + getHeight();
	}

	public boolean containsCursor() {
		return contains((int) InputTracker.getCursorX(), (int) InputTracker.getCursorY());
	}

	public void requestReassembly() {
		if (parent != null) {
			parent.requestReassembly();
		} else {
			handleReassemblyRequest();
		}
	}

	protected void handleReassemblyRequest() {
		// To be overridden
	}

	protected synchronized final void assemble(RenderTarget target) {
		if (width == 0 || height == 0) {
			return;
		}

		if (!isValid()) {
			validate();
		}

		try {
			assembleSelf(target);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not assemble Component %s", this);
		}

		assembleChildren(target);

		try {
			postAssembleSelf(target);
		} catch (Exception e) {
			throw CrashReports.report(e, "Post-assembly failed for Component %s", this);
		}
	}

	protected void assembleSelf(RenderTarget target) {
		// To be overridden
	}

	protected void postAssembleSelf(RenderTarget target) {
		// To be overridden
	}

	protected void assembleChildren(RenderTarget target) {
		getChildren().forEach(child -> child.assemble(target));
	}

	// /**
	// * Returns a component that displays this component in its center.
	// * @return a {@link Aligner} initialized to center this component
	// */
	// public Component center() {
	// return new Aligner(this);
	// }
	//
	// /**
	// * Returns a component that aligns this component.
	// * @return a {@link Aligner} initialized with this component
	// */
	// public Component align(double x, double y) {
	// return new Aligner(this, x, y);
	// }
	//
	// /**
	// * Returns a component that allows scrolling this component
	// * @return a {@link Scroller} initialized with this component
	// */
	// public Component scroller() {
	// return new Scroller(this);
	// }

}
