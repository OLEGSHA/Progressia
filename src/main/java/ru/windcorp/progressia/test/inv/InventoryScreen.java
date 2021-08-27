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
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Components;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.Layout;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;

public class InventoryScreen extends Component {

	public static class CursorFollower extends Component {
		
		public CursorFollower(Component child) {
			super("CursorFollower");
			addChild(child);
			setLayout(null);
			
			Vec2i size = child.getPreferredSize();
			child.setBounds(-size.x / 2, -size.y / 2, size);
			layoutSelf();
		}

		@Override
		protected void assembleChildren(RenderTarget target) {
			Renderable renderable = getChildren().get(0).assembleToRenderable();

			target.addCustomRenderer(renderer -> {
				renderer.pushTransform().translate(
					(float) InputTracker.getCursorX(),
					(float) InputTracker.getCursorY(),
					0
				);
				
				renderable.render(renderer);
				
				renderer.popTransform();

			});
		}

	}

	private final Component hands;

	public InventoryScreen(String name, Component mainInventory, EntityDataPlayer player) {
		super(name);
		
		setLayout(new LayoutFill(0));
		
		addChild(new Panel(name + ".Background", new LayoutAlign(10), Colors.toVector(0x66000000), null));
		
		Panel mainInventoryPanel = new Panel(name + ".Content", new LayoutFill(20));
		mainInventoryPanel.addChild(mainInventory);
		addChild(Components.center(mainInventoryPanel));
		
		this.hands = createHands(name, player);
		addChild(new CursorFollower(this.hands));
	}

	private Component createHands(String name, EntityDataPlayer player) {

		SlotComponent leftHand = new SlotComponent(name + ".HandLeft", player.getLeftHand().getSlot(0));
		SlotComponent rightHand = new SlotComponent(name + ".HandRight", player.getRightHand().getSlot(0));
		
		final int gap = 15;
		final int offset = 60;
		
		Component hands = new Group(name + ".Hands", null, leftHand, rightHand);
		hands.setLayout(new Layout() {
			
			@Override
			public void layout(Component c) {
				Vec2i leftSize = leftHand.getPreferredSize();
				Vec2i rightSize = rightHand.getPreferredSize();
				
				leftHand.setBounds(c.getX(), c.getY(), leftSize);
				rightHand.setBounds(c.getX() + leftSize.x + gap, c.getY() + offset, rightSize);
			}
			
			@Override
			public Vec2i calculatePreferredSize(Component c) {
				Vec2i leftSize = leftHand.getPreferredSize();
				Vec2i rightSize = rightHand.getPreferredSize();
				
				return new Vec2i(
					leftSize.x + gap + rightSize.x,
					Math.max(leftSize.y + offset, rightSize.y + offset)
				);
			}
			
		});
		
		return hands;
	}

}
