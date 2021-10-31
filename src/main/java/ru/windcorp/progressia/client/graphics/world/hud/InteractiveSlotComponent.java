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

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.input.WheelScrollEvent;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.item.ItemDataContainer;
import ru.windcorp.progressia.common.world.item.Items;
import ru.windcorp.progressia.common.world.item.inventory.ItemContainer;
import ru.windcorp.progressia.common.world.item.inventory.ItemContainerMixed;
import ru.windcorp.progressia.common.world.item.inventory.ItemSlot;

public class InteractiveSlotComponent extends Button {

	private static final double MIN_PICK_ALL_DELAY = Units.get("0.5 s");

	private double lastMainAction = Double.NEGATIVE_INFINITY;

	private final SlotComponent slotComponent;
	private final HUDWorkspace workspace;

	public InteractiveSlotComponent(String name, ItemContainer container, int index, HUDWorkspace workspace) {
		this(name, new SlotComponent(name, container, index), workspace);
	}

	public InteractiveSlotComponent(SlotComponent component, HUDWorkspace workspace) {
		this(component.getName() + ".Interactive", component, workspace);
	}

	public InteractiveSlotComponent(String name, SlotComponent component, HUDWorkspace workspace) {
		super(name, null, null);
		this.slotComponent = component;
		this.workspace = workspace;

		Vec2i size = slotComponent.getPreferredSize().add(2 * BORDER);
		setPreferredSize(size);

		addChild(this.slotComponent);
		setLayout(new LayoutFill(MARGIN));

		addListeners();
	}

	private void addListeners() {
		addAction(button -> onMainAction());

		addListener(KeyMatcher.ofRightMouseButton(), this::onAltAction);

		addListener(WheelScrollEvent.class, event -> {
			if (event.hasVerticalMovement()) {
				onSingleMoveAction(event.isDown());
				return true;
			}
			return false;
		});
	}

	private void onMainAction() {
		ItemSlot handSlot = workspace.getHand().slot();
		ItemSlot invSlot = getSlotOrAllocate();
		if (invSlot == null) {
			return;
		}

		boolean success = false;

		double now = GraphicsInterface.getTime();
		if (now - lastMainAction < MIN_PICK_ALL_DELAY) {
			lastMainAction = Double.NEGATIVE_INFINITY;
			pickAll(handSlot);
			success = true;
		} else {
			lastMainAction = now;
		}

		if (!success) {
			success = Items.pour(handSlot, invSlot) != 0;
		}

		if (!success) {
			success = Items.swap(handSlot, invSlot);
		}

		if (!success && handSlot.isEmpty()) {
			success = Items.pour(invSlot, handSlot) != 0;
		}

		if (success) {
			requestReassembly();
		}
		
		cleanContainerUpIfNecessary();
	}

	private void pickAll(ItemSlot handSlot) {
		for (ItemSlot invSlot : getSlot().getContainer()) {
			Items.pour(invSlot, handSlot);
			if (handSlot.isEmpty()) {
				break;
			}
		}
	}

	private void onAltAction() {
		ItemSlot handSlot = workspace.getHand().slot();
		ItemSlot invSlot = getSlotOrAllocate();
		if (invSlot == null) {
			return;
		}
		
		boolean success = false;

		if (handSlot.isEmpty()) {
			success = tryToOpen(invSlot);
		}

		if (!success && handSlot.isEmpty()) {
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

		cleanContainerUpIfNecessary();
	}

	private boolean tryToOpen(ItemSlot invSlot) {
		if (invSlot.getAmount() != 1) {
			return false;
		}
		if (!(invSlot.getContents() instanceof ItemDataContainer)) {
			return false;
		}

		ItemDataContainer item = (ItemDataContainer) invSlot.getContents();
		return item.open(workspace.getPlayerEntity()) != null;
	}

	private void onSingleMoveAction(boolean fromHand) {
		ItemSlot handSlot = workspace.getHand().slot();
		ItemSlot invSlot = getSlot();

		ItemSlot from = fromHand ? handSlot : invSlot;
		ItemSlot into = fromHand ? invSlot : handSlot;

		if (Items.pour(from, into, 1) != 0) {
			requestReassembly();
		}
	}

	public ItemSlot getSlot() {
		return slotComponent.getSlot();
	}

	private ItemSlot getSlotOrAllocate() {
		ItemSlot slot = slotComponent.getSlot();
		
		if (slot == null) {

			int slotIndex = slotComponent.getSlotIndex();
			ItemContainer uncastContainer = slotComponent.getSlotContainer();

			assert slotIndex >= 0 : "Slot index is negative: " + slotIndex;
			assert slotIndex >= uncastContainer.getSlotCount()
				: "Slot index is valid (" + slotIndex + ") but container does not provide a slot";

			if (uncastContainer instanceof ItemContainerMixed) {
				ItemContainerMixed container = (ItemContainerMixed) uncastContainer;
				container.addSlots(slotIndex - container.getSlotCount() + 1);
				
				slot = slotComponent.getSlot();
				assert slot != null : "Could not allocate slot for index " + slotIndex;
			} else {
				// Leave slot null
			}
			
		}
		
		return slot;
	}

	private void cleanContainerUpIfNecessary() {
		ItemContainer container = slotComponent.getSlotContainer();
		if (container instanceof ItemContainerMixed) {
			((ItemContainerMixed) container).cleanUpSlots();
		}
	}

}
