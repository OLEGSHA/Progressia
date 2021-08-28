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

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.BasicButton;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Components;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.Layout;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.item.ItemContainer;
import ru.windcorp.progressia.common.world.item.ItemData;
import ru.windcorp.progressia.common.world.item.ItemSlot;

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

	private final ItemContainer leftHand;
	private final ItemContainer rightHand;

	public InventoryScreen(String name, InventoryComponent mainInventory, EntityDataPlayer player) {
		super(name);

		setLayout(new LayoutFill(0));

		addChild(new Panel(name + ".Background", new LayoutAlign(10), Colors.toVector(0x66000000), null));

		Panel mainInventoryPanel = new Panel(name + ".Content", new LayoutFill(20));
		mainInventoryPanel.addChild(mainInventory);
		addChild(Components.center(mainInventoryPanel));

		this.leftHand = player.getLeftHand();
		this.rightHand = player.getRightHand();
		addChild(new CursorFollower(createHands(name, leftHand, rightHand)));

		addListeners(mainInventory);
	}

	private Component createHands(String name, ItemContainer leftHand, ItemContainer rightHand) {

		SlotComponent leftComponent = new SlotComponent(name + ".HandLeft", leftHand, 0);
		SlotComponent rightComponent = new SlotComponent(name + ".HandRight", rightHand, 0);

		final int gap = 15;
		final int offset = 60;

		Component hands = new Group(name + ".Hands", null, leftComponent, rightComponent);
		hands.setLayout(new Layout() {

			@Override
			public void layout(Component c) {
				Vec2i leftSize = leftComponent.getPreferredSize();
				Vec2i rightSize = rightComponent.getPreferredSize();

				leftComponent.setBounds(c.getX(), c.getY(), leftSize);
				rightComponent.setBounds(c.getX() + leftSize.x + gap, c.getY() + offset, rightSize);
			}

			@Override
			public Vec2i calculatePreferredSize(Component c) {
				Vec2i leftSize = leftComponent.getPreferredSize();
				Vec2i rightSize = rightComponent.getPreferredSize();

				return new Vec2i(
					leftSize.x + gap + rightSize.x,
					Math.max(leftSize.y + offset, rightSize.y + offset)
				);
			}

		});

		return hands;
	}

	private void addListeners(InventoryComponent mainInventory) {
		Consumer<BasicButton> pickIntoLeft = createPickAction(leftHand.getSlot(0), rightHand.getSlot(0));

		for (DecoratedSlotComponent component : mainInventory.getSlots()) {
			component.addAction(pickIntoLeft);
		}
	}

	private Consumer<BasicButton> createPickAction(ItemSlot toWithoutCtrl, ItemSlot toWithCtrl) {
		return button -> {

			boolean hasCtrl = InputTracker.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
				|| InputTracker.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL);
			
			ItemSlot to = hasCtrl ? toWithCtrl : toWithoutCtrl;
			ItemSlot from = ((DecoratedSlotComponent) button).getSlot();

			ItemData fromData = from.getContents();
			ItemData toData = to.getContents();

			from.setContents(toData);
			to.setContents(fromData);

			requestReassembly();

		};
	}

}
