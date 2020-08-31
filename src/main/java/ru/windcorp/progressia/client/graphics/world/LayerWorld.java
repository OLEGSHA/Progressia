/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics.world;

import org.lwjgl.glfw.GLFW;

import glm.mat._3.Mat3;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.comms.controls.InputBasedControls;
import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.CursorMoveEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.common.util.Vectors;

public class LayerWorld extends Layer {
	
	private final Vec3 velocity = new Vec3();
	
	private final Mat3 angMat = new Mat3();

	private int movementForward = 0;
	private int movementRight = 0;
	private int movementUp = 0;
	
	private final WorldRenderHelper helper = new WorldRenderHelper();
	
	private final Client client;
	private final InputBasedControls inputBasedControls;

	public LayerWorld(Client client) {
		super("World");
		this.client = client;
		this.inputBasedControls = new InputBasedControls(client);
	}
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void doValidate() {
		// Do nothing
	}
	
	@Override
	protected void doRender() {
		client.getCamera().apply(helper);
		renderWorld();
		helper.reset();
		
		angMat.set().rotateZ(-client.getCamera().getYaw());
		
		Vec3 movement = Vectors.grab3();
		
		movement.set(movementForward, -movementRight, 0);
		if (movementForward != 0 && movementRight != 0) movement.normalize();
		angMat.mul_(movement); // bug in jglm
		movement.z = movementUp;
		movement.mul(0.1f);
		movement.sub(velocity);
		movement.mul(0.1f);
		velocity.add(movement);
		
		Vectors.release(movement);
		
		Vec3 velCopy = Vectors.grab3().set(velocity);
		
		velCopy.mul((float) (GraphicsInterface.getFrameLength() * 60));
		client.getCamera().move(velCopy);
		
		Vectors.release(velCopy);
	}

	private void renderWorld() {
		this.client.getWorld().render(helper);
	}
	
	@Override
	protected void handleInput(Input input) {
		if (input.isConsumed()) return;
		
		InputEvent event = input.getEvent();
		
		if (event instanceof KeyEvent) {
			if (onKeyEvent((KeyEvent) event)) {
				input.consume();
			}
		} else if (event instanceof CursorMoveEvent) {
			onMouseMoved((CursorMoveEvent) event);
			input.consume();
		}
		
		if (!input.isConsumed()) {
			inputBasedControls.handleInput(input);
		}
	}
	
	private boolean flag = true;
	
	private boolean onKeyEvent(KeyEvent event) {
		if (event.isRepeat()) return false;
		
		int multiplier = event.isPress() ? 1 : -1;
		
		switch (event.getKey()) {
		case GLFW.GLFW_KEY_W:
			movementForward += +1 * multiplier;
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
			movementUp += +1 * multiplier;
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			movementUp += -1 * multiplier;
			break;
			
		case GLFW.GLFW_KEY_ESCAPE:
			if (!event.isPress()) return false;
			
			if (flag) {
				GLFW.glfwSetInputMode(GraphicsBackend.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			} else {
				GLFW.glfwSetInputMode(GraphicsBackend.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			}
			
			flag = !flag;
			break;
			
		default:
			return false;
		}
		
		return true;
	}
	
	private void onMouseMoved(CursorMoveEvent event) {
		if (!flag) return;
		
		final float yawScale = 0.002f;
		final float pitchScale = yawScale;
		
		client.getCamera().turn(
				(float) (event.getChangeY() * pitchScale),
				(float) (event.getChangeX() * yawScale)
		);
	}

}
