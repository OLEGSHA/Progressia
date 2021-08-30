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
package ru.windcorp.progressia.test.inv;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;

public class HandSlots extends Component {

	private final SlotComponent left;
	private final SlotComponent right;

	/**
	 * Right is 0, left is 1
	 */
	private float selection = 0;
	private static final float ANIMATION_SPEED = 10f;

	public HandSlots(String name, SlotComponent left, SlotComponent right) {
		super(name);
		this.left = left;
		this.right = right;

		addChild(left);
		addChild(right);
		setLayout(null);

		centerAtOrigin(left);
		centerAtOrigin(right);
		layoutSelf();
	}

	private static void centerAtOrigin(Component component) {
		Vec2i size = component.getPreferredSize();
		component.setBounds(-size.x / 2, -size.y / 2, size);
	}

	@Override
	protected void assembleChildren(RenderTarget target) {

		Renderable leftRenderable = left.assembleToRenderable();
		Renderable rightRenderable = right.assembleToRenderable();

		target.addCustomRenderer(renderer -> {

			tickAnimation();
			
			renderer.pushTransform().translate(
				(float) InputTracker.getCursorX(),
				(float) InputTracker.getCursorY(),
				0
			);

			if (selection > 0.5) {
				renderHand(renderer, leftRenderable, selection, -1);
				renderHand(renderer, rightRenderable, 1 - selection, +1);
			} else {
				renderHand(renderer, rightRenderable, 1 - selection, +1);
				renderHand(renderer, leftRenderable, selection, -1);
			}
			
			renderer.popTransform();

		});

	}
	
	private float stretch(float t, float zero, float one) {
		return zero * (1 - t) + one * t;
	}

	private void renderHand(ShapeRenderHelper renderer, Renderable renderable, float selected, float direction) {

		float offsetX = direction * stretch(selected, 40, 0);
		float offsetY = direction * stretch(selected, 30, 0);
		float scale = selected < 0.5 ? stretch(selected * 2, 0.6f, 1.0f) : 1;
		
		renderer.pushTransform().translate(offsetX, offsetY, 0).scale(scale);
		renderable.render(renderer);
		renderer.popTransform();

	}

	private void tickAnimation() {
		float desired = InventoryScreen.isLeftHandSelected() ? 1 : 0;
		float difference = selection - desired;
		selection += difference * (1 - Math.exp(ANIMATION_SPEED * GraphicsInterface.getFrameLength()));
	}

}
