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
 
package ru.windcorp.progressia.client.graphics;

import java.util.concurrent.atomic.AtomicBoolean;

import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.bus.Input;

public abstract class Layer {

	private final String name;

	private boolean hasInitialized = false;

	private final AtomicBoolean isValid = new AtomicBoolean(false);
	
	/**
	 * Represents various requests that a {@link Layer} can make regarding the
	 * presence of a visible cursor. The value of the highest layer that is not
	 * {@link #INDIFFERENT} is used.
	 */
	public static enum CursorPolicy {
		/**
		 * Require that a cursor is visible.
		 */
		REQUIRE,

		/**
		 * The {@link Layer} should not affect the presence or absence of a
		 * visible cursor; lower layers should be consulted.
		 */
		INDIFFERENT,

		/**
		 * Forbid a visible cursor.
		 */
		FORBID
	}
	
	private CursorPolicy cursorPolicy = CursorPolicy.INDIFFERENT;

	public Layer(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Layer " + name;
	}
	
	public CursorPolicy getCursorPolicy() {
		return cursorPolicy;
	}
	
	public void setCursorPolicy(CursorPolicy cursorPolicy) {
		this.cursorPolicy = cursorPolicy;
	}

	void render() {
		GraphicsInterface.startNextLayer();

		validate();

		if (!hasInitialized) {
			initialize();
			hasInitialized = true;
		}

		doRender();
	}

	void validate() {
		if (isValid.compareAndSet(false, true)) {
			doValidate();
		}
	}

	public void invalidate() {
		isValid.set(false);
	}

	protected abstract void initialize();

	protected abstract void doValidate();

	protected abstract void doRender();

	protected abstract void handleInput(Input input);

	protected int getWidth() {
		return GraphicsInterface.getFrameWidth();
	}

	protected int getHeight() {
		return GraphicsInterface.getFrameHeight();
	}
	
	protected void onAdded() {
		// Do nothing
	}
	
	protected void onRemoved() {
		// Do nothing
	}

}
