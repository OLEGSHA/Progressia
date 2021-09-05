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
import ru.windcorp.progressia.client.graphics.ExponentAnimation;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.world.hud.SlotComponent;
import ru.windcorp.progressia.client.world.entity.SpeciesRender;
import ru.windcorp.progressia.client.world.entity.SpeciesRenderRegistry;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.entity.SpeciesData.Hand;
import ru.windcorp.progressia.common.world.item.ItemContainerHand;

public class HandSlots extends Component {

	private class CursorBoundSlot {

		private final SlotComponent component;
		private final Renderable renderable;

		/**
		 * 0 is not selected, 1 is selected
		 */
		private final ExponentAnimation selection = new ExponentAnimation(10, 0);
		private final double angle;

		public CursorBoundSlot(SlotComponent component, double angle) {
			this.component = component;
			this.angle = angle;

			Vec2i size = component.getPreferredSize();
			component.setBounds(-size.x / 2, -size.y / 2, size);

			this.renderable = component.assembleToRenderable();
			
			if (player.getHandCount() == 1) {
				// Disable opening animation hint
				selection.setValue(1);
			}
		}

		public void render(ShapeRenderHelper renderer) {

			float target = player.getSelectedHand() == component.getSlot().getContainer() ? 1 : 0;
			float sel = selection.updateForFrame(target);

			float distance = HandSlots.this.distance * (1 - sel);
			float x = (float) Math.cos(angle) * distance;
			float y = (float) Math.sin(angle) * distance;
			float scale = 0.5f + 0.5f * sel;

			renderer.pushTransform().translate(x, y, 0).scale(scale);

			boolean popColor = false;
			if (sel > 0.5f && component.getSlot().isEmpty()) {
				renderer.pushColorMultiplier().mul(1, 1, 1, 1 - 2 * (sel - 0.5f));
				popColor = true;
			}

			renderable.render(renderer);

			if (popColor) {
				renderer.popColorMultiplier();
			}

			renderer.popTransform();

		}

	}

	private final EntityDataPlayer player;
	
	private final float distance = 50;
	private final double startAngle = Math.PI / 4;
	private final double endAngle = -3 * Math.PI / 4;

	private final CursorBoundSlot[] slots;

	public HandSlots(String name, EntityDataPlayer player) {
		super(name);
		this.player = player;

		this.slots = new CursorBoundSlot[player.getHandCount()];
		
		// This produces NaN when there is only one hand, but then it is unused
		double angleStep = (endAngle - startAngle) / (slots.length - 1);

		double angle = startAngle;
		for (int i = 0; i < slots.length; ++i) {

			SpeciesRender speciesRender = SpeciesRenderRegistry.getInstance().get(player);

			ItemContainerHand container = player.getHand(i);
			Hand hand = container.getHand();

			SlotComponent component = new SlotComponent(name + ".Hand" + hand.getName(), container, 0)
				.setBackground(speciesRender.getHandBackground(hand));
			
			addChild(component);
			
			slots[i] = new CursorBoundSlot(component, angle);
			
			angle += angleStep;
			
		}

		setLayout(null);
	}

	@Override
	protected void assembleChildren(RenderTarget target) {

		target.addCustomRenderer(renderer -> {

			renderer.pushTransform().translate(
				(float) InputTracker.getCursorX(),
				(float) InputTracker.getCursorY(),
				0
			);

			for (CursorBoundSlot slot : slots) {
				slot.render(renderer);
			}

			renderer.popTransform();

		});

	}

}
