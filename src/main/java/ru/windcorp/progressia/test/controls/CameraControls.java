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

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.comms.controls.ControlTriggerRegistry;
import ru.windcorp.progressia.client.comms.controls.ControlTriggers;
import ru.windcorp.progressia.client.graphics.input.CursorMoveEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.world.Camera;
import ru.windcorp.progressia.client.graphics.world.EntityAnchor;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class CameraControls {

	{
		reset();
	}

	public void reset() {
		// No instance fields; futureproofing
	}

	public void registerControls() {
		ControlTriggerRegistry triggers = ControlTriggerRegistry.getInstance();

		triggers.register(
			ControlTriggers.localOf(
				"Test:TurnCameraWithCursor",
				CursorMoveEvent.class,
				this::turnCameraWithCursor
			)
		);

		triggers.register(
			ControlTriggers.localOf(
				"Test:SwitchCameraMode",
				KeyEvent.class,
				this::switchCameraMode,
				new KeyMatcher("F5")::matches
			)
		);

		NoclipCamera.register();

		triggers.register(
			ControlTriggers.localOf(
				"Test:ToggleNoclip",
				KeyEvent.class,
				NoclipCamera::toggleNoclip,
				new KeyMatcher("V")::matches
			)
		);
	}

	private void turnCameraWithCursor(CursorMoveEvent event) {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		Camera.Anchor cameraAnchor = ClientState.getInstance().getCamera().getAnchor();
		if (!(cameraAnchor instanceof EntityAnchor)) {
			return;
		}

		EntityData player = ((EntityAnchor) cameraAnchor).getEntity();

		final double yawScale = -0.002f;
		final double pitchScale = -yawScale;
		final double pitchExtremum = Math.PI / 2 * 0.995f;

		double yawChange = event.getChangeX() * yawScale;
		double pitchChange = event.getChangeY() * pitchScale;

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

	private void switchCameraMode() {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}

		if (ClientState.getInstance().getCamera().hasAnchor()) {
			ClientState.getInstance().getCamera().selectNextMode();
		}
	}

}
