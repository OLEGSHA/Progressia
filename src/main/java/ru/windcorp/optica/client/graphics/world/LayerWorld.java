/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.graphics.world;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;

import glm.mat._3.Mat3;
import glm.vec._3.Vec3;
import ru.windcorp.optica.client.graphics.Layer;
import ru.windcorp.optica.client.graphics.backend.GraphicsBackend;
import ru.windcorp.optica.client.graphics.backend.GraphicsInterface;
import ru.windcorp.optica.client.graphics.input.CursorMoveEvent;
import ru.windcorp.optica.client.graphics.input.KeyEvent;
import ru.windcorp.optica.client.world.WorldRender;
import ru.windcorp.optica.common.block.BlockData;
import ru.windcorp.optica.common.block.BlockDataRegistry;
import ru.windcorp.optica.common.world.WorldData;

public class LayerWorld extends Layer {
	
	public static Camera tmp_the_camera;
	
	private final Camera camera = new Camera(
			new Vec3(8, 8, 8),
			0, 0,
			(float) Math.toRadians(70)
	);
	
	private final Vec3 velocity = new Vec3();
	private final Vec3 tmp = new Vec3();
	
	private final Mat3 angMat = new Mat3();
	
	private int movementX = 0;
	private int movementY = 0;
	private int movementZ = 0;
	
	private static final boolean I_WANT_TO_THROW_UP = false;
	private float shakeParam = 0;
	
	private final WorldRenderHelper helper = new WorldRenderHelper();
	
	private final WorldRender world = new WorldRender(new WorldData());

	public LayerWorld() {
		super("World");
		tmp_the_camera = camera;
	}
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		GraphicsInterface.subscribeToInputEvents(this);
	}
	
	@Override
	protected void doValidate() {
		// Do nothing
	}
	
	@Override
	protected void doRender() {
		camera.apply(helper);
		renderWorld();
		helper.reset();
		
		angMat.set().rotateY(-camera.getYaw());

		tmp.set(movementX, 0, movementZ);
		if (movementX != 0 && movementZ != 0) tmp.normalize();
		angMat.mul_(tmp); // bug in jglm
		tmp.y = movementY;
		tmp.mul(0.1f);
		tmp.sub(velocity);
		tmp.mul(0.1f);
		velocity.add(tmp);
		tmp.set(velocity);
		tmp.mul((float) (GraphicsInterface.getFrameLength() * 60));
		camera.move(tmp);
		
		if (I_WANT_TO_THROW_UP) {
			shakeParam += tmp.set(tmp.x, 0, tmp.z).length() * 1.5f;
			float vel = tmp.set(velocity).set(tmp.x, 0, tmp.z).length() * 0.7f;
			
			helper.pushViewTransform().translate(
					(float) Math.sin(shakeParam) * vel,
					(float) Math.sin(2 * shakeParam) * vel,
					0.25f
			).rotateZ(
					(float) Math.sin(shakeParam) * vel * 0.15f
			);
		}
	}

	private void renderWorld() {
		world.render(helper);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	private boolean flag = true;
	
	@Subscribe
	public void onKeyEvent(KeyEvent event) {
		if (event.isRepeat()) return;
		
		int multiplier = event.isPress() ? 1 : -1;
		
		switch (event.getKey()) {
		case GLFW.GLFW_KEY_W:
			movementZ += -1 * multiplier;
			break;
		case GLFW.GLFW_KEY_S:
			movementZ += +1 * multiplier;
			break;
		case GLFW.GLFW_KEY_A:
			movementX += -1 * multiplier;
			break;
		case GLFW.GLFW_KEY_D:
			movementX += +1 * multiplier;
			break;
		case GLFW.GLFW_KEY_SPACE:
			movementY += +1 * multiplier;
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			movementY += -1 * multiplier;
			break;
			
		case GLFW.GLFW_KEY_ESCAPE:
			if (!event.isPress()) return;
			
			if (flag) {
				GLFW.glfwSetInputMode(GraphicsBackend.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			} else {
				GLFW.glfwSetInputMode(GraphicsBackend.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			}
			
			flag = !flag;
			break;
			
		case GLFW.GLFW_KEY_G:
			if (!event.isPress()) return;
			
			BlockData[][][] data = world.getData().getChunk(0, 0, 0).tmp_getBlocks();
			if (data[0][0][0].getId().equals("Test:Stone")) {
				data[0][0][0] = BlockDataRegistry.get("Test:Glass");
			} else {
				data[0][0][0] = BlockDataRegistry.get("Test:Stone");
			}
			world.getChunk(0, 0, 0).markForUpdate();
			
			break;
		}
	}
	
	@Subscribe
	public void onMouseMoved(CursorMoveEvent event) {
		if (!flag) return;
		
		final float yawScale = 0.002f;
		final float pitchScale = yawScale;
		
		camera.turn(
				(float) (event.getChangeY() * pitchScale),
				(float) (event.getChangeX() * yawScale)
		);
	}

}
