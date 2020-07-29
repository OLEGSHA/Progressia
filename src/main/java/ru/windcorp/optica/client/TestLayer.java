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
package ru.windcorp.optica.client;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;

import glm.mat._3.Mat3;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.optica.client.graphics.Layer;
import ru.windcorp.optica.client.graphics.backend.GraphicsBackend;
import ru.windcorp.optica.client.graphics.backend.GraphicsInterface;
import ru.windcorp.optica.client.graphics.input.KeyEvent;
import ru.windcorp.optica.client.graphics.input.CursorMoveEvent;
import ru.windcorp.optica.client.graphics.model.Model;
import ru.windcorp.optica.client.graphics.model.DynamicModel;
import ru.windcorp.optica.client.graphics.model.Shapes;
import ru.windcorp.optica.client.graphics.texture.SimpleTexture;
import ru.windcorp.optica.client.graphics.texture.Sprite;
import ru.windcorp.optica.client.graphics.texture.Texture;
import ru.windcorp.optica.client.graphics.texture.TextureManager;
import ru.windcorp.optica.client.graphics.world.Camera;
import ru.windcorp.optica.client.graphics.world.WorldRenderer;

public class TestLayer extends Layer {
	
	private final Model model;
	
	private final Camera camera = new Camera(
			new Vec3(),
			0, (float) Math.PI,
			(float) Math.toRadians(70)
	);
	
	private final Vec3 velocity = new Vec3();
	private final Vec3 tmp = new Vec3();
	
	private final Mat3 angMat = new Mat3();
	
	private int movementX = 0;
	private int movementY = 0;
	private int movementZ = 0;

	public TestLayer() {
		super("Test");
		
		Texture top = qtex("grass_top");
		Texture side = qtex("grass_side");
		Texture bottom = qtex("grass_bottom");
		
		model = new DynamicModel(DynamicModel.builder()
				.addDynamicPart(
						new Shapes.PppBuilder(top, bottom, side, side, side, side)
						.create()
				)
		) {
			@Override
			protected void getDynamicTransform(int shapeIndex, Mat4 result) {
				result.translate(0, 0, +5);
			}
		};
	}
	
	private Texture qtex(String name) {
		return new SimpleTexture(new Sprite(TextureManager.load(name, false)));
	}
	
	@Override
	protected void initialize() {
		GraphicsInterface.subscribeToInputEvents(this);
	}
	
	private final WorldRenderer renderer = new WorldRenderer();

	@Override
	protected void doRender() {
		camera.apply(renderer);
		
		angMat.set().rotateY(-camera.getYaw());

		tmp.set(movementX, 0, movementZ);
		angMat.mul_(tmp); // bug in jglm
		tmp.y = movementY;
		tmp.mul(0.1f);
		tmp.sub(velocity);
		tmp.mul(0.1f);
		velocity.add(tmp);
		camera.move(velocity);
		
		model.render(renderer);
		
		renderer.reset();
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
