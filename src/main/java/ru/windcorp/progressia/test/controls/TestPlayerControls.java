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

import java.util.List;

import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.comms.controls.ControlTriggerRegistry;
import ru.windcorp.progressia.client.comms.controls.ControlTriggers;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.test.LayerButtonTest;
import ru.windcorp.progressia.test.TestMusicPlayer;

public class TestPlayerControls {

	private static final TestPlayerControls INSTANCE = new TestPlayerControls();

	public static TestPlayerControls getInstance() {
		return INSTANCE;
	}
	
	private final MovementControls movementControls = new MovementControls();
	private final CameraControls cameraControls = new CameraControls();
	private final InteractionControls interactionControls = new InteractionControls();
	private final InventoryControls inventoryControls = new InventoryControls();
	
	{
		reset();
	}
	
	public static void resetInstance() {
		INSTANCE.reset();
	}
	
	private void reset() {
		movementControls.reset();
		cameraControls.reset();
		interactionControls.reset();
		inventoryControls.reset();
	}

	public void applyPlayerControls() {
		movementControls.applyPlayerControls();
	}
	
	public void registerControls() {
		movementControls.registerControls();
		cameraControls.registerControls();
		interactionControls.registerControls();
		inventoryControls.registerControls();
		
		ControlTriggerRegistry triggers = ControlTriggerRegistry.getInstance();
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:PauseGame",
				KeyEvent.class,
				this::pauseGame,
				new KeyMatcher("Escape")::matches
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:ToggleFullscreen",
				KeyEvent.class,
				this::toggleFullscreen,
				new KeyMatcher("F11")::matches
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:ToggleVSync",
				KeyEvent.class,
				this::toggleVSync,
				new KeyMatcher("F12")::matches
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:ToggleDebugLayer",
				KeyEvent.class,
				() -> ClientState.getInstance().toggleDebugLayer(),
				new KeyMatcher("F3")::matches
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:SwitchLanguage",
				KeyEvent.class,
				this::switchLanguage,
				new KeyMatcher("L")::matches
			)
		);

		triggers.register(
			ControlTriggers.localOf(
				"Test:StartNextMusic",
				KeyEvent.class,
				TestMusicPlayer::startNextNow,
				new KeyMatcher("M")::matches
			)
		);
	}

	private void pauseGame() {
		GUI.addTopLayer(new LayerButtonTest());
	}
	
	private void toggleFullscreen() {
		GraphicsInterface.makeFullscreen(!GraphicsBackend.isFullscreen());
	}
	
	private void toggleVSync() {
		GraphicsBackend.setVSyncEnabled(!GraphicsBackend.isVSyncEnabled());
	}

	private void switchLanguage() {
		Localizer localizer = Localizer.getInstance();
		List<String> languages = localizer.getLanguages();
		
		int index = languages.indexOf(localizer.getLanguage());
		
		if (index == languages.size() - 1) {
			index = 0;
		} else {
			index++;
		}
		
		localizer.setLanguage(languages.get(index));
	}

	public EntityData getEntity() {
		return getPlayer().getEntity();
	}

	public LocalPlayer getPlayer() {
		return ClientState.getInstance().getLocalPlayer();
	}
	
	public MovementControls getMovementControls() {
		return movementControls;
	}
	
	public CameraControls getCameraControls() {
		return cameraControls;
	}
	
	public InteractionControls getInteractionControls() {
		return interactionControls;
	}
	
	public InventoryControls getInventoryControls() {
		return inventoryControls;
	}

	public BlockData getSelectedBlock() {
		return interactionControls.getSelectedBlock();
	}

	public TileData getSelectedTile() {
		return interactionControls.getSelectedTile();
	}

	public boolean isBlockSelected() {
		return interactionControls.isBlockSelected();
	}
	
	public boolean isFlying() {
		return movementControls.isFlying();
	}
	
	public boolean isSprinting() {
		return movementControls.isSprinting();
	}

}
