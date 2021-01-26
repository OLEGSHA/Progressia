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
import glm.mat._3.Mat3;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import org.lwjgl.glfw.GLFW;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.input.CursorMoveEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.WheelScrollEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.FloatMathUtil;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.ServerState;

public class TestPlayerControls {

	private static final TestPlayerControls INSTANCE = new TestPlayerControls();

	public static TestPlayerControls getInstance() {
		return INSTANCE;
	}

	private static final double MODE_SWITCH_MAX_DELAY = 300 * Units.MILLISECONDS;
	private static final double MODE_SPRINT_SWITCH_MAX_DELAY = 100 * Units.MILLISECONDS;
	private static final double MIN_JUMP_DELAY = 300 * Units.MILLISECONDS;

	// Horizontal and vertical max control speed when flying
	private static final float FLYING_SPEED = Units.get("6 m/s");

	// (0; 1], 1 is instant change, 0 is no control authority
	private static final float FLYING_CONTROL_AUTHORITY = Units.get("2 1/s");

	// Horizontal max control speed when walking
	private static final float WALKING_SPEED = Units.get("4 m/s");

	// Horizontal max control speed when sprinting
	private static final float SPRINTING_SPEED = Units.get("6 m/s");

	// (0; 1], 1 is instant change, 0 is no control authority
	private static final float WALKING_CONTROL_AUTHORITY = Units.get("15 1/s");

	// Vertical velocity instantly added to player when they jump
	private static final float JUMP_VELOCITY = 5f * Units.METERS_PER_SECOND;

	private boolean isFlying = true;
	private boolean isSprinting = false;

	private int movementForward = 0;
	private int movementRight = 0;
	private int movementUp = 0;

	private double lastSpacePress = Double.NEGATIVE_INFINITY;
	private double lastSprintPress = Double.NEGATIVE_INFINITY;

	private boolean captureMouse = true;
	private boolean useMinecraftGravity = false;

	private int selectedBlock = 0;
	private int selectedTile = 0;
	private boolean isBlockSelected = true;

	private LayerTestGUI debugLayer = null;
	private Runnable updateCallback = null;

	public void applyPlayerControls() {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		EntityData player = getEntity();

		final float speed, authority;

		if (isFlying) {
			speed = FLYING_SPEED;
			authority = FLYING_CONTROL_AUTHORITY;
		} else {
			speed = isSprinting ? SPRINTING_SPEED : WALKING_SPEED;
			authority = WALKING_CONTROL_AUTHORITY;
		}

		Mat3 angMat = new Mat3().identity().rotateZ(player.getYaw());
		Vec3 desiredVelocity = new Vec3(movementForward, -movementRight, 0);

		if (movementForward != 0 && movementRight != 0)
			desiredVelocity.normalize();
		angMat.mul_(desiredVelocity); // bug in jglm, .mul() and mul_() are
										// swapped
		desiredVelocity.z = movementUp;
		desiredVelocity.mul(speed);

		Vec3 change = new Vec3()
			.set(desiredVelocity)
			.sub(player.getVelocity())
			.mul((float) Math.exp(-authority * GraphicsInterface.getFrameLength()))
			.negate()
			.add(desiredVelocity);

		if (!isFlying) {
			change.z = player.getVelocity().z;
		}

		player.getVelocity().set(change);

		// THIS IS TERRIBLE TEST
		EntityData serverEntity = ServerState.getInstance().getWorld().getData()
			.getEntity(TestContent.PLAYER_ENTITY_ID);
		if (serverEntity != null) {
			serverEntity.setPosition(player.getPosition());
		}

	}

	public void handleInput(Input input) {
		InputEvent event = input.getEvent();

		if (event instanceof KeyEvent) {
			if (onKeyEvent((KeyEvent) event)) {
				input.consume();
			}
		} else if (event instanceof CursorMoveEvent) {
			onMouseMoved((CursorMoveEvent) event);
			input.consume();
		} else if (event instanceof WheelScrollEvent) {
			onWheelScroll((WheelScrollEvent) event);
			input.consume();
		}
	}

	private boolean onKeyEvent(KeyEvent event) {
		if (event.isRepeat())
			return false;
		int multiplier = event.isPress() ? 1 : -1;
		switch (event.getKey()) {
		case GLFW.GLFW_KEY_W:
			movementForward += +1 * multiplier;
			handleSprint(event);
			break;
		case GLFW.GLFW_KEY_S:
			movementForward += -1 * multiplier;
			break;
		case GLFW.GLFW_KEY_A:
			movementRight += -1 * multiplier;
			break;
		case GLFW.GLFW_KEY_D:
			movementRight += +1 * multiplier;
			break;
		case GLFW.GLFW_KEY_SPACE:
			handleSpace(multiplier);
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			handleShift(multiplier);
			break;
		case GLFW.GLFW_KEY_ESCAPE:
			if (!event.isPress())
				return false;
			handleEscape();
			break;

		case GLFW.GLFW_KEY_F11:
			if (!event.isPress())
				return false;
			GraphicsInterface.makeFullscreen(!GraphicsBackend.isFullscreen());
			updateGUI();
			break;

		case GLFW.GLFW_KEY_F3:
			if (!event.isPress())
				return false;
			handleDebugLayerSwitch();
			break;

		case GLFW.GLFW_KEY_F5:
			if (!event.isPress())
				return false;
			handleCameraMode();
			break;

		case GLFW.GLFW_KEY_G:
			if (!event.isPress())
				return false;
			handleGravitySwitch();
			break;

		case GLFW.GLFW_KEY_L:
			if (!event.isPress())
				return false;
			handleLanguageSwitch();
			break;

		case GLFW.GLFW_MOUSE_BUTTON_3:
			if (!event.isPress())
				return false;
			switchPlacingMode();
			break;

		default:
			return false;
		}

		return true;
	}

	private void handleSpace(int multiplier) {
		boolean isPressed = multiplier > 0;

		double timeSinceLastSpacePress = GraphicsInterface.getTime() - lastSpacePress;

		if (isPressed && timeSinceLastSpacePress < MODE_SWITCH_MAX_DELAY) {
			isSprinting = false;
			isFlying = !isFlying;
			updateGUI();
			movementUp = +1;
		} else {
			if (isFlying) {
				movementUp += +1 * multiplier;
			} else {
				if (isPressed && timeSinceLastSpacePress > MIN_JUMP_DELAY) {
					jump();
				}
			}
		}

		lastSpacePress = GraphicsInterface.getTime();
	}

	private void handleSprint(KeyEvent event) {

		double timeSinceLastSpacePress = GraphicsInterface.getTime() - lastSprintPress;

		if (event.isPress() && timeSinceLastSpacePress < MODE_SPRINT_SWITCH_MAX_DELAY && !isFlying) {
			isSprinting = !isSprinting;
			updateGUI();
		}

		lastSprintPress = GraphicsInterface.getTime();

		if (isSprinting && event.isRelease()) {
			isSprinting = false;
			updateGUI();
		}
	}

	private void jump() {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		getEntity().getVelocity().add(0, 0, JUMP_VELOCITY * (useMinecraftGravity ? 2 : 1));
	}

	private void handleShift(int multiplier) {
		if (isFlying) {
			movementUp += -1 * multiplier;
		}
	}

	private void handleEscape() {
		if (captureMouse) {
			GLFW.glfwSetInputMode(GraphicsBackend.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		} else {
			GLFW.glfwSetInputMode(GraphicsBackend.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		}

		captureMouse = !captureMouse;
		updateGUI();
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

	private void handleGravitySwitch() {
		useMinecraftGravity = !useMinecraftGravity;
		updateGUI();
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
		if (!captureMouse)
			return;

		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		final float yawScale = -0.002f;
		final float pitchScale = yawScale;

		EntityData player = getEntity();

		normalizeAngles(
			player.getDirection().add(
				(float) (event.getChangeX() * yawScale),
				(float) (event.getChangeY() * pitchScale)
			)
		);
	}

	private void normalizeAngles(Vec2 dir) {
		// Normalize yaw
		dir.x = FloatMathUtil.normalizeAngle(dir.x);

		// Clamp pitch
		dir.y = Glm.clamp(
			dir.y,
			-FloatMathUtil.PI_F / 2,
			+FloatMathUtil.PI_F / 2
		);
	}

	private void onWheelScroll(WheelScrollEvent event) {
		if (event.hasHorizontalMovement()) {
			switchPlacingMode();
		}
		if (InputTracker.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			switchPlacingMode();
			return;
		}

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

	private void switchPlacingMode() {
		isBlockSelected = !isBlockSelected;
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

	public boolean isFlying() {
		return isFlying;
	}

	public boolean isSprinting() {
		return isSprinting;
	}

	public boolean isMouseCaptured() {
		return captureMouse;
	}

	public boolean useMinecraftGravity() {
		return useMinecraftGravity;
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

}
