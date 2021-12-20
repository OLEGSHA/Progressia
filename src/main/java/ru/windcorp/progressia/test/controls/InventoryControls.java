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
package ru.windcorp.progressia.test.controls;

import org.lwjgl.glfw.GLFW;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.comms.controls.ControlTriggerRegistry;
import ru.windcorp.progressia.client.comms.controls.ControlTriggers;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;

public class InventoryControls {
	
	/**
	 * The minimum duration of a ctrl stroke for it to be consider holding the key down
	 */
	private static final double MIN_CTRL_HOLD_LENGTH = Units.get("200 ms");
	
	private double lastCtrlPress;
	
	{
		reset();
	}

	public void reset() {
		lastCtrlPress = Double.NEGATIVE_INFINITY;
	}

	public void registerControls() {
		ControlTriggerRegistry triggers = ControlTriggerRegistry.getInstance();

		triggers.register(
			ControlTriggers.localOf(
				"Test:ToggleInventory",
				KeyEvent.class,
				this::toggleInventory,
				new KeyMatcher("E")::matches
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:CloseInventory",
				KeyEvent.class,
				this::toggleInventory,
				new KeyMatcher("Escape")::matches,
				e -> ClientState.getInstance().getHUD().isInventoryShown()
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:HideHUD",
				KeyEvent.class,
				this::switchHUD,
				new KeyMatcher("F1")::matches
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:SwitchHandsWithCtrl",
				KeyEvent.class,
				this::switchHandsWithCtrl,
				e -> !e.isRepeat(),
				e -> e.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL || e.getKey() == GLFW.GLFW_KEY_RIGHT_CONTROL
			)
		);
	}
	
	private void toggleInventory() {
		Client client = ClientState.getInstance();
		if (client == null || !client.isReady())
			return;

		client.getHUD().setInventoryShown(!client.getHUD().isInventoryShown());
	}

	private void switchHUD() {
		Client client = ClientState.getInstance();
		if (client == null || !client.isReady())
			return;

		client.getHUD().setHidden(!client.getHUD().isHidden());
	}
	
	private void switchHandsWithCtrl(KeyEvent event) {
		int change = 0;
		
		if (event.isPress()) {
			change = +1;
			lastCtrlPress = event.getTime();
		} else {
			if (event.getTime() - lastCtrlPress > MIN_CTRL_HOLD_LENGTH) {
				change = -1;
				lastCtrlPress = Double.NEGATIVE_INFINITY;
			}
		}
		
		if (event.hasShift()) {
			change *= -1;
		}
		
		switchHands(change);
	}
	
	private void switchHands(int change) {
		if (change == 0) {
			return;
		}
		
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}
		
		EntityDataPlayer entity = ClientState.getInstance().getLocalPlayer().getEntity();
		
		int selected = entity.getSelectedHandIndex();
		int maxSelected = entity.getHandCount() - 1;
		
		selected += change;
		if (selected < 0) {
			selected = maxSelected;
		} else if (selected > maxSelected) {
			selected = 0;
		}
		
		entity.setSelectedHandIndexNow(selected);
	}


}
