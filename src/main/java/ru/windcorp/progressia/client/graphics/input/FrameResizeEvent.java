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

package ru.windcorp.progressia.client.graphics.input;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;

public class FrameResizeEvent extends InputEvent {

	private final Vec2i newSize = new Vec2i();

	protected FrameResizeEvent(int newWidth, int newHeight, double time) {
		super(time);
		this.newSize.set(newWidth, newHeight);
	}

	protected FrameResizeEvent(Vec2i newSize, double time) {
		this(newSize.x, newSize.y, time);
	}

	public int getNewWidth() {
		return getNewSize().x;
	}

	public int getNewHeight() {
		return getNewSize().y;
	}

	public Vec2i getNewSize() {
		return newSize;
	}

	public int getPreviousWidth() {
		return getPreviousSize().x;
	}

	public int getPreviousHeight() {
		return getPreviousSize().y;
	}

	public Vec2i getPreviousSize() {
		return GraphicsInterface.getFrameSize();
	}

	@Override
	public FrameResizeEvent snapshot() {
		return new StaticFrameResizeEvent(getNewSize(), getPreviousSize(), getTime());
	}

	private static class StaticFrameResizeEvent extends FrameResizeEvent {

		private final Vec2i previousSize;

		public StaticFrameResizeEvent(Vec2i newSize, Vec2i previousSize, double time) {
			super(newSize, time);
			this.previousSize = previousSize;
		}

		@Override
		public Vec2i getPreviousSize() {
			return previousSize;
		}

	}

}
