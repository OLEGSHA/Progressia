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

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import glm.Glm;
import glm.mat._3.Mat3;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.comms.controls.InputBasedControls;
import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.backend.FaceCulling;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.CursorMoveEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.CollisionClock;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.collision.CompoundCollisionModel;
import ru.windcorp.progressia.common.collision.colliders.Collider;
import ru.windcorp.progressia.common.util.FloatMathUtils;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.test.AABBRenderer;

public class LayerWorld extends Layer {
	
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
		if (client.getLocalPlayer() != null) {
			tmp_handleControls();
		}
		
		Camera camera = client.getCamera();
		if (camera.hasAnchor()) {
			renderWorld();
		}
	}

	private void tmp_handleControls() {
		EntityData player = client.getLocalPlayer();
		
		angMat.identity().rotateZ(player.getYaw());
		
		Vec3 movement = Vectors.grab3();
		
		// Horizontal and vertical max control speed
		final float movementSpeed = 0.1f * 60.0f;
		// (0; 1], 1 is instant change, 0 is no control authority
		final float controlAuthority = 0.1f;
		
		movement.set(movementForward, -movementRight, 0);
		if (movementForward != 0 && movementRight != 0) movement.normalize();
		angMat.mul_(movement); // bug in jglm, .mul() and mul_() are swapped
		movement.z = movementUp;
		movement.mul(movementSpeed);
		movement.sub(player.getVelocity());
		movement.mul(controlAuthority);
		player.getVelocity().add(movement);
		
		Vectors.release(movement);
	}

	private void renderWorld() {
		client.getCamera().apply(helper);
		FaceCulling.push(true);
		
		this.client.getWorld().render(helper);
		
		tmp_doEveryFrame();
		
		FaceCulling.pop();
		helper.reset();
	}
	
	private final Collider.ColliderWorkspace tmp_colliderWorkspace = new Collider.ColliderWorkspace();
	private final List<Collideable> tmp_collideableList = new ArrayList<>();
	
	private static final boolean RENDER_AABBS = true;
	
	private void tmp_doEveryFrame() {
		try {
			if (RENDER_AABBS) {
				for (EntityData data : this.client.getWorld().getData().getEntities()) {
					CollisionModel model = data.getCollisionModel();
					if (model instanceof AABB) {
						AABBRenderer.renderAABB((AABB) model, helper);
					} else if (model instanceof CompoundCollisionModel) {
						AABBRenderer.renderAABBsInCompound((CompoundCollisionModel) model, helper);
					}
				}
			}
			
			tmp_collideableList.clear();
			tmp_collideableList.addAll(this.client.getWorld().getData().getEntities());
			
			Collider.performCollisions(
					tmp_collideableList,
					new CollisionClock() {
						private float t = 0;
						@Override
						public float getTime() {
							return t;
						}
						
						@Override
						public void advanceTime(float change) {
							t += change;
						}
					},
					(float) GraphicsInterface.getFrameLength(),
					tmp_colliderWorkspace
			);
			
			final float frictionCoeff = 1 - 1e-2f;
			
			for (EntityData data : this.client.getWorld().getData().getEntities()) {
				data.getVelocity().mul(frictionCoeff);
			}
		} catch (Exception e) {
			System.exit(31337);
		}
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
			
		case GLFW.GLFW_KEY_F5:
			if (!event.isPress()) return false;
			
			if (client.getCamera().hasAnchor()) {
				client.getCamera().selectNextMode();
			}
			break;
			
		default:
			return false;
		}
		
		return true;
	}
	
	private void onMouseMoved(CursorMoveEvent event) {
		if (!flag) return;
		
		final float yawScale = -0.002f;
		final float pitchScale = yawScale;

		EntityData player = client.getLocalPlayer();
		
		if (player != null) {
			normalizeAngles(player.getDirection().add(
					(float) (event.getChangeX() * yawScale),
					(float) (event.getChangeY() * pitchScale)
			));
		}
	}

	private void normalizeAngles(Vec2 dir) {
		// Normalize yaw
		dir.x = FloatMathUtils.normalizeAngle(dir.x);
		
		// Clamp pitch
		dir.y = Glm.clamp(
				dir.y, -FloatMathUtils.PI_F/2, +FloatMathUtils.PI_F/2
		);
	}

}
