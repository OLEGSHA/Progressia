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
package ru.windcorp.progressia.client.graphics.world.hud;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.ExponentAnimation;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderHorizontal;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutHorizontal;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.world.entity.SpeciesRender;
import ru.windcorp.progressia.client.world.entity.SpeciesRenderRegistry;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.entity.SpeciesData.Hand;

public class HandsHUD extends Component {

	public class ScaledSlotComponent extends Component {

		private final SlotComponent slotComponent;
		private final ExponentAnimation selected = new ExponentAnimation(10, 1);

		public ScaledSlotComponent(SlotComponent component) {
			super(component.getName() + ".Scaled");
			this.slotComponent = component;
			addChild(component);
			
			Vec2i size = slotComponent.getPreferredSize();
			setPreferredSize(size.x * 2, size.y * 2);
			slotComponent.setBounds(-size.x / 2, 0, size);
		}

		@Override
		protected void assembleChildren(RenderTarget target) {
			Renderable renderable = slotComponent.assembleToRenderable();

			target.addCustomRenderer(renderer -> {
				float scale = manager.getHand() == slotComponent.getSlot().getContainer() ? 2 : 1;
				renderer.pushTransform()
					.translate(getX() + getWidth() / 2, getY(), 0)
					.scale(selected.updateForFrame(scale));

				renderable.render(renderer);

				renderer.popTransform();
			});
		}

	}

	public enum Side {
		LEFT("Left", LayoutBorderHorizontal.LEFT, 0.0),
		RIGHT("Right", LayoutBorderHorizontal.RIGHT, 1.0),
		CENTER("Center", LayoutBorderHorizontal.CENTER, 0.5);

		private final String ccName;
		private final Object lbhHint;
		private final double align;

		private Side(String ccName, Object lbhHint, double align) {
			this.ccName = ccName;
			this.lbhHint = lbhHint;
			this.align = align;
		}
	}

	private final HUDManager manager;

	public HandsHUD(String name, HUDManager manager) {
		super(name);
		this.manager = manager;

		EntityDataPlayer entity = manager.getPlayerEntity();
		String speciesId = entity.getSpecies().getId();
		SpeciesRender speciesRender = SpeciesRenderRegistry.getInstance().get(speciesId);

		Map<Side, Component> containers = Maps.toMap(
			Arrays.asList(Side.values()),
			side -> new Group(name + "." + side.ccName, new LayoutHorizontal(15))
		);

		for (int i = 0; i < entity.getHandCount(); ++i) {

			Hand hand = entity.getSpecies().getHands().get(i);

			SlotComponent display = new SlotComponent(name + "." + hand.getName(), entity.getHand(i), 0)
				.setBackground(speciesRender.getHandBackground(hand), this::shouldRenderHandPlaceholder)
				.setScale(2);

			Component scaledDisplay = new ScaledSlotComponent(display);

			containers.get(speciesRender.getHandSide(hand)).addChild(scaledDisplay);

		}

		setLayout(new LayoutBorderHorizontal());

		containers.forEach((side, comp) -> {
			addChild(
				new Group(name + "." + side.ccName + ".Aligner", new LayoutAlign(side.align, 0.5, 0), comp)
					.setLayoutHint(side.lbhHint)
			);
		});

	}

	private boolean shouldRenderHandPlaceholder() {
		return manager.isInventoryShown();
	}

}
