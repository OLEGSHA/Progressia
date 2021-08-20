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

import com.google.common.eventbus.Subscribe;

import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.flat.AssembledFlatLayer;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;

public class LayerTestUI extends AssembledFlatLayer {

	public LayerTestUI() {
		super("TestUI");

		GraphicsInterface.subscribeToInputEvents(this);
	}

	private boolean drawUI = true;

	@Override
	protected void assemble(RenderTarget target) {
		if (drawUI) {
			drawCross(target);
		}
	}

	private void drawCross(RenderTarget target) {
		int cx = getWidth() / 2;
		int cy = getHeight() / 2;

		final int length = 15;
		final int thickness = 6;
		final int borderSize = 2;
		final Vec4 borderColor = Colors.BLACK;
		final Vec4 fillColor = Colors.WHITE;

		target.fill(
			cx - length - thickness / 2,
			cy - thickness / 2,
			2 * length + thickness,
			thickness,
			borderColor
		);

		target.fill(
			cx - thickness / 2,
			cy - length - thickness / 2,
			thickness,
			2 * length + thickness,
			borderColor
		);

		target.fill(
			cx - length - thickness / 2 + borderSize,
			cy - thickness / 2 + borderSize,
			2 * length + thickness - 2 * borderSize,
			thickness - 2 * borderSize,
			fillColor
		);

		target.fill(
			cx - thickness / 2 + borderSize,
			cy - length - thickness / 2 + borderSize,
			thickness - 2 * borderSize,
			2 * length + thickness - 2 * borderSize,
			fillColor
		);
	}

	@Override
	protected void handleInput(Input input) {
		// Do nothing
	}

	@Subscribe
	public void onKeyEvent(KeyEvent event) {
		if (!event.isPress() || event.getKey() != GLFW.GLFW_KEY_F1) {
			return;
		}

		drawUI = !drawUI;
		invalidate();
	}

}
