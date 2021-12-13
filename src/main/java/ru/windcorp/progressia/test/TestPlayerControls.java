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

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import org.lwjgl.glfw.GLFW;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.CursorMoveEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.WheelScrollEvent;
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.test.controls.MovementControls;

public class TestPlayerControls {

	private static final TestPlayerControls INSTANCE = new TestPlayerControls();

	public static TestPlayerControls getInstance() {
		return INSTANCE;
	}
	
	private final MovementControls movementControls = new MovementControls();

	private int selectedBlock = 0;
	private int selectedTile = 0;
	private boolean isBlockSelected = true;

	private LayerTestGUI debugLayer = null;
	private Runnable updateCallback = null;
	
	{
		reset();
	}
	
	public static void resetInstance() {
		INSTANCE.reset();
	}
	
	private void reset() {
		movementControls.reset();

		debugLayer = null;
		updateCallback = null;
		selectedBlock = 0;         
		selectedTile = 0;          
		isBlockSelected = true;
	}

	public void applyPlayerControls() {
		movementControls.applyPlayerControls();
	}
	
	public void registerControls() {
		movementControls.registerControls();
	}

	public void handleInput(InputEvent event) {
		if (event instanceof KeyEvent) {
			if (onKeyEvent((KeyEvent) event)) {
				event.consume();
			}
		} else if (event instanceof CursorMoveEvent) {
			onMouseMoved((CursorMoveEvent) event);
			event.consume();
		}
	}

	private boolean onKeyEvent(KeyEvent event) {
		if (!event.isPress())
			return false;
		switch (event.getKey()) {
		case GLFW.GLFW_KEY_ESCAPE:
			handleEscape();
			break;

		case GLFW.GLFW_KEY_F11:
			GraphicsInterface.makeFullscreen(!GraphicsBackend.isFullscreen());
			updateGUI();
			break;

		case GLFW.GLFW_KEY_F12:
			GraphicsBackend.setVSyncEnabled(!GraphicsBackend.isVSyncEnabled());
			updateGUI();
			break;

		case GLFW.GLFW_KEY_F3:
			handleDebugLayerSwitch();
			break;

		case GLFW.GLFW_KEY_F5:
			handleCameraMode();
			break;

		case GLFW.GLFW_KEY_L:
			handleLanguageSwitch();
			break;

		default:
			return false;
		}

		return true;
	}

	private void handleEscape() {
		GUI.addTopLayer(new LayerButtonTest());
	}

	private void handleDebugLayerSwitch() {
		if (debugLayer == null) {
			this.debugLayer = new LayerTestGUI();
			this.updateCallback = debugLayer.getUpdateCallback();
		}

		if (GUI.getLayers().contains(debugLayer)) {
			GUI.removeLayer(debugLayer);
		} else {
			GUI.addTopLayer(debugLayer);
		}
	}

	private void handleCameraMode() {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		if (ClientState.getInstance().getCamera().hasAnchor()) {
			ClientState.getInstance().getCamera().selectNextMode();
			updateGUI();
		}
	}

	private void handleLanguageSwitch() {
		Localizer localizer = Localizer.getInstance();
		if (localizer.getLanguage().equals("ru-RU")) {
			localizer.setLanguage("en-US");
		} else {
			localizer.setLanguage("ru-RU");
		}

		updateGUI();
	}

	private void onMouseMoved(CursorMoveEvent event) {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		final double yawScale = -0.002f;
		final double pitchScale = -yawScale;
		final double pitchExtremum = Math.PI/2 * 0.95f;
		
		double yawChange = event.getChangeX() * yawScale;
		double pitchChange = event.getChangeY() * pitchScale;

		EntityData player = getEntity();
		
		double startPitch = player.getPitch();
		double endPitch = startPitch + pitchChange;
		endPitch = Glm.clamp(endPitch, -pitchExtremum, +pitchExtremum);
		pitchChange = endPitch - startPitch;
		
		Mat4 mat = Matrices.grab4();
		Vec3 lookingAt = Vectors.grab3();		
		Vec3 rightVector = Vectors.grab3();

		rightVector.set(player.getLookingAt()).cross(player.getUpVector()).normalize();
		
		mat.identity()
			.rotate((float) yawChange, player.getUpVector())
			.rotate((float) pitchChange, rightVector);
		
		VectorUtil.applyMat4(player.getLookingAt(), mat, lookingAt);
		player.setLookingAt(lookingAt);
		
		Vectors.release(rightVector);
		Vectors.release(lookingAt);
		Matrices.release(mat);
	}

	public void switchPlacingMode() {
		isBlockSelected = !isBlockSelected;
		updateGUI();
	}
	
	public void selectNextBlockOrTile(WheelScrollEvent event) {
		if (isBlockSelected) {
			selectedBlock += event.isUp() ? +1 : -1;

			int size = TestContent.PLACEABLE_BLOCKS.size();

			if (selectedBlock < 0) {
				selectedBlock = size - 1;
			} else if (selectedBlock >= size) {
				selectedBlock = 0;
			}
		} else {
			selectedTile += event.isUp() ? +1 : -1;

			int size = TestContent.PLACEABLE_TILES.size();

			if (selectedTile < 0) {
				selectedTile = size - 1;
			} else if (selectedTile >= size) {
				selectedTile = 0;
			}
		}

		updateGUI();
	}

	public EntityData getEntity() {
		return getPlayer().getEntity();
	}

	public LocalPlayer getPlayer() {
		return ClientState.getInstance().getLocalPlayer();
	}

	private void updateGUI() {
		if (this.updateCallback != null) {
			this.updateCallback.run();
		}
	}
	
	public MovementControls getMovementControls() {
		return movementControls;
	}

	public BlockData getSelectedBlock() {
		return TestContent.PLACEABLE_BLOCKS.get(selectedBlock);
	}

	public TileData getSelectedTile() {
		return TestContent.PLACEABLE_TILES.get(selectedTile);
	}

	public boolean isBlockSelected() {
		return isBlockSelected;
	}
	
	public boolean isFlying() {
		return movementControls.isFlying();
	}
	
	public boolean isSprinting() {
		return movementControls.isSprinting();
	}

}
