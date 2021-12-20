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

import glm.mat._3.Mat3;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.comms.controls.ControlTriggerRegistry;
import ru.windcorp.progressia.client.comms.controls.ControlTriggers;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.world.Camera;
import ru.windcorp.progressia.client.graphics.world.EntityAnchor;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.ServerState;

public class MovementControls {

	/**
	 * Max delay between space presses that can toggle flying
	 */
	private static final double FLYING_SWITCH_MAX_DELAY = Units.get("300 ms");

	/**
	 * Max delay between W presses that can toggle sprinting
	 */
	private static final double SPRINTING_SWITCH_MAX_DELAY = Units.get("300 ms");

	/**
	 * Min delay between jumps
	 */
	private static final double JUMP_MIN_DELAY = Units.get("300 ms");

	/**
	 * Horizontal and vertical max control speed when flying
	 */
	private static final float FLYING_SPEED = Units.get("6 m/s");

	/**
	 * (0; 1], 1 is instant change, 0 is no control authority
	 */
	private static final float FLYING_CONTROL_AUTHORITY = Units.get("2 1/s");

	/**
	 * Horizontal max control speed when walking
	 */
	private static final float WALKING_SPEED = Units.get("4 m/s");

	/**
	 * Horizontal max control speed when sprinting
	 */
	private static final float SPRINTING_SPEED = Units.get("6 m/s");

	/**
	 * (0; 1], 1 is instant change, 0 is no control authority
	 */
	private static final float WALKING_CONTROL_AUTHORITY = Units.get("15 1/s");

	/**
	 * Vertical velocity instantly added to player when they jump
	 */
	private static final float JUMP_VELOCITY = Units.get("5 m/s");

	private boolean isFlying;
	private boolean isSprinting;

	private double lastSpacePress;
	private double lastSprintPress;

	{
		reset();
	}

	public void applyPlayerControls() {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		Camera.Anchor cameraAnchor = ClientState.getInstance().getCamera().getAnchor();
		if (!(cameraAnchor instanceof EntityAnchor)) {
			return;
		}

		EntityData player = ((EntityAnchor) cameraAnchor).getEntity();

		boolean isFlying = this.isFlying || player.getId().equals("Test:NoclipCamera");
		boolean isSprinting = this.isSprinting || player.getId().equals("Test:NoclipCamera");

		final float speed, authority;

		if (isFlying) {
			speed = FLYING_SPEED;
			authority = FLYING_CONTROL_AUTHORITY;
		} else {
			speed = isSprinting ? SPRINTING_SPEED : WALKING_SPEED;
			authority = WALKING_CONTROL_AUTHORITY;
		}

		Mat3 movementTransform = getMovementTransform(player, null);
		Vec3 desiredVelocity = getDesiredVelocity(movementTransform, speed, isFlying);
		Vec3 newVelocity = getNewVelocity(
			desiredVelocity,
			player.getVelocity(),
			authority,
			player.getUpVector(),
			isFlying
		);
		player.getVelocity().set(newVelocity);

		tmp_syncServerEntity();
	}

	private void tmp_syncServerEntity() {
		// THIS IS TERRIBLE TEST
		EntityData serverEntity = ServerState.getInstance().getWorld().getData()
			.getEntity(ClientState.getInstance().getLocalPlayer().getEntityId());
		if (serverEntity != null) {
			EntityData clientEntity = ClientState.getInstance().getLocalPlayer().getEntity();

			clientEntity.copy(serverEntity);
			serverEntity.setLookingAt(clientEntity.getLookingAt());
			serverEntity.setUpVector(clientEntity.getUpVector());
			serverEntity.setPosition(clientEntity.getPosition());
			serverEntity.setVelocity(clientEntity.getVelocity());
		}
	}

	private Mat3 getMovementTransform(EntityData player, Mat3 mat) {
		if (mat == null) {
			mat = new Mat3();
		}

		Vec3 f = player.getForwardVector(null);
		Vec3 u = player.getUpVector();
		Vec3 s = f.cross_(u);

		//@formatter:off
		return mat.set(
			f.x, f.y, f.z,
			s.x, s.y, s.z,
			u.x, u.y, u.z
		);
		//@formatter:on
	}

	private Vec3 getDesiredVelocity(Mat3 movementTransform, float speed, boolean isFlying) {
		int forward = 0;
		int right = 0;
		int up = 0;

		forward += InputTracker.isKeyPressed(GLFW.GLFW_KEY_W) ? 1 : 0;
		forward -= InputTracker.isKeyPressed(GLFW.GLFW_KEY_S) ? 1 : 0;

		right += InputTracker.isKeyPressed(GLFW.GLFW_KEY_D) ? 1 : 0;
		right -= InputTracker.isKeyPressed(GLFW.GLFW_KEY_A) ? 1 : 0;

		if (isFlying) {
			up += InputTracker.isKeyPressed(GLFW.GLFW_KEY_SPACE) ? 1 : 0;
			up -= InputTracker.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 1 : 0;
		}

		Vec3 desiredVelocity = new Vec3(forward, right, 0);
		if (forward != 0 && right != 0) {
			desiredVelocity.normalize();
		}
		desiredVelocity.z = up;

		// bug in jglm, .mul() and .mul_() are swapped
		movementTransform.mul_(desiredVelocity);
		desiredVelocity.mul(speed);

		return desiredVelocity;
	}

	private Vec3 getNewVelocity(Vec3 desiredVelocity, Vec3 oldVelocity, float authority, Vec3 up, boolean isFlying) {

		// newVelocity = oldVelocity + small change toward desiredVelocity
		Vec3 newVelocity = new Vec3()
			.set(desiredVelocity)
			.sub(oldVelocity)
			.mul((float) Math.exp(-authority * GraphicsInterface.getFrameLength()))
			.negate()
			.add(desiredVelocity);

		// If we aren't flying, don't change vertical component
		if (!isFlying) {
			Vec3 wantedVertical = VectorUtil.projectOnVector(oldVelocity, up, null);
			VectorUtil.projectOnSurface(newVelocity, up);
			newVelocity.add(wantedVertical);
		}

		return newVelocity;
	}

	public void reset() {
		isFlying = true;
		isSprinting = false;
		lastSpacePress = Double.NEGATIVE_INFINITY;
		lastSprintPress = Double.NEGATIVE_INFINITY;
	}

	public void registerControls() {
		ControlTriggerRegistry triggers = ControlTriggerRegistry.getInstance();

		triggers.register(
			ControlTriggers.localOf(
				"Test:JumpOrToggleFlight",
				KeyEvent.class,
				this::handleSpacePress,
				new KeyMatcher("Space")::matches
			)
		);

		triggers.register(
			ControlTriggers.localOf(
				"Test:ToggleSprint",
				KeyEvent.class,
				this::toggleSprint,

				new KeyMatcher("W")::matches,
				e -> !isFlying
			)
		);

		triggers.register(
			ControlTriggers.localOf(
				"Test:DisableSprint",
				KeyEvent.class,
				this::disableSprint,

				new KeyMatcher("W")::matchesIgnoringAction,
				KeyEvent::isRelease
			)
		);
	}

	private void handleSpacePress(KeyEvent e) {
		if (
			ClientState.getInstance().getCamera().getAnchor() instanceof EntityAnchor
				&& ((EntityAnchor) ClientState.getInstance().getCamera().getAnchor()).getEntity().getId()
					.equals("Test:NoclipCamera")
		) {
			return;
		}

		double timeSinceLastSpacePress = e.getTime() - lastSpacePress;

		if (timeSinceLastSpacePress < FLYING_SWITCH_MAX_DELAY) {
			isSprinting = false;
			isFlying = !isFlying;
		} else if (!isFlying && timeSinceLastSpacePress >= JUMP_MIN_DELAY) {
			jump();
		}

		lastSpacePress = e.getTime();
	}

	private void jump() {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		EntityData player = ClientState.getInstance().getLocalPlayer().getEntity();
		assert player != null;

		Vec3 up = player.getUpVector();

		player.getVelocity().add(
			up.x * JUMP_VELOCITY,
			up.y * JUMP_VELOCITY,
			up.z * JUMP_VELOCITY
		);
	}

	private void toggleSprint(KeyEvent e) {
		if (e.getTime() - lastSprintPress < SPRINTING_SWITCH_MAX_DELAY) {
			isSprinting = !isSprinting;
		}
		lastSprintPress = e.getTime();
	}

	private void disableSprint(KeyEvent e) {
		isSprinting = false;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public boolean isSprinting() {
		return isSprinting;
	}

}
