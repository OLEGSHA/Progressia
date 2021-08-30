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
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.gui.BasicButton;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Components;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.WheelScrollEvent;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.item.ItemContainer;
import ru.windcorp.progressia.common.world.item.ItemSlot;
import ru.windcorp.progressia.common.world.item.Items;

public class InventoryScreen extends Component {
	
	private static final double MIN_PICK_ALL_DELAY = Units.get("0.5 s");

	public static boolean isLeftHandSelected() {
		return InputTracker.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
			|| InputTracker.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL);
	}

	private final ItemContainer leftHand;
	private final ItemContainer rightHand;
	private final InventoryComponent mainInventory;
	
	private double lastLeftClick = Double.NEGATIVE_INFINITY;
	private ItemSlot lastLeftClickSlot = null;

	public InventoryScreen(String name, InventoryComponent mainInventory, EntityDataPlayer player) {
		super(name);

		this.mainInventory = mainInventory;
		this.leftHand = player.getLeftHand();
		this.rightHand = player.getRightHand();

		setLayout(new LayoutFill(0));

		addChild(new Panel(name + ".Background", new LayoutAlign(10), Colors.toVector(0x66000000), null));

		Panel mainInventoryPanel = new Panel(name + ".Content", new LayoutFill(20));
		mainInventoryPanel.addChild(mainInventory);
		addChild(Components.center(mainInventoryPanel));

		SlotComponent leftComponent = new SlotComponent(name + ".HandLeft", leftHand, 0);
		SlotComponent rightComponent = new SlotComponent(name + ".HandRight", rightHand, 0);
		
		addChild(new HandSlots(name + ".Hands", leftComponent, rightComponent));

		addListeners(mainInventory);
		
		mainInventory.focusNext();
	}

	private void addListeners(InventoryComponent mainInventory) {
		
		ItemSlot left = leftHand.getSlot(0);
		ItemSlot right = rightHand.getSlot(0);
		
		Supplier<ItemSlot> handSlot = () -> isLeftHandSelected() ? left : right;
		
		Consumer<BasicButton> pickAll = createPickAllAction(handSlot);

		for (DecoratedSlotComponent component : mainInventory.getSlots()) {
			component.addAction(pickAll);
			component.addListener(KeyEvent.class, createRMBAction(component.getSlot(), handSlot));
			component.addListener(WheelScrollEvent.class, createWheelAction(component.getSlot(), handSlot));
		}
	}

	private Consumer<BasicButton> createPickAllAction(Supplier<ItemSlot> handSlotChooser) {
		return button -> {
			
			ItemSlot handSlot = handSlotChooser.get();
			ItemSlot invSlot = ((DecoratedSlotComponent) button).getSlot();
			
			boolean success = false;
			
			double now = GraphicsInterface.getTime();
			if (lastLeftClickSlot == invSlot && now - lastLeftClick < MIN_PICK_ALL_DELAY) {
				
				lastLeftClickSlot = null;
				lastLeftClick = Double.NEGATIVE_INFINITY;
				
				pickAll(handSlot);
				
				success = true;
				
			} else {
				lastLeftClick = now;
				lastLeftClickSlot = invSlot;
			}
			
			if (!success && !leftHand.getSlot(0).isEmpty() && rightHand.getSlot(0).isEmpty()) {
				success = Items.pour(leftHand.getSlot(0), invSlot) != 0;
			}
			
			if (!success) {
				success = Items.pour(handSlot, invSlot) != 0;
			}
			
			if (!success) {
				success = Items.swap(handSlot, invSlot);
			}

			if (success) {
				requestReassembly();
			}

		};
	}

	private void pickAll(ItemSlot handSlot) {
		for (DecoratedSlotComponent component : mainInventory.getSlots()) {
			
			Items.pour(component.getSlot(), handSlot);
			if (handSlot.isEmpty()) {
				break;
			}
			
		}
	}

	private InputListener<KeyEvent> createRMBAction(ItemSlot invSlot, Supplier<ItemSlot> handSlotChooser) {
		return input -> {
			
			if (input.isPress() && input.isRightMouseButton()) {
				ItemSlot handSlot = handSlotChooser.get();

				boolean success = false;
				
				if (handSlot.isEmpty()) {
					success = Items.pour(invSlot, handSlot, invSlot.getAmount() / 2) != 0;
				}
				
				if (!success) {
					success = Items.pour(handSlot, invSlot, 1) != 0;
				}
				
				if (!success) {
					success = Items.swap(handSlot, invSlot);
				}

				if (success) {
					requestReassembly();
				}
				
				return true;
			}
			
			return false;
			
		};
	}
	

	private InputListener<WheelScrollEvent> createWheelAction(ItemSlot invSlot, Supplier<ItemSlot> handSlotChooser) {
		return input -> {
			
			if (!input.hasVerticalMovement()) {
				return false;
			}
			
			ItemSlot handSlot = handSlotChooser.get();
			
			ItemSlot from = input.isDown() ? handSlot : invSlot;
			ItemSlot into = input.isDown() ? invSlot : handSlot;
			
			if (Items.pour(from, into, 1) != 0) {
				requestReassembly();
			}
			
			return true;
			
		};
	}

}
