package ru.windcorp.progressia.test;

import org.lwjgl.glfw.GLFW;

import glm.Glm;
import glm.mat._3.Mat3;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.CursorMoveEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.FloatMathUtils;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class TestPlayerControls {
	
	private static final TestPlayerControls INSTANCE = new TestPlayerControls();
	
	public static TestPlayerControls getInstance() {
		return INSTANCE;
	}
	
	private TestPlayerControls() {}
	
	private static final double MODE_SWITCH_MAX_DELAY = 100 * Units.MILLISECONDS;
	private static final double MIN_JUMP_DELAY = 200 * Units.MILLISECONDS;

	// Horizontal and vertical max control speed when flying
	private static final float FLYING_SPEED = 6.0f * Units.METERS_PER_SECOND;
	
	// (0; 1], 1 is instant change, 0 is no control authority
	private static final float FLYING_CONTROL_AUTHORITY = 0.05f;
	
	// Horizontal and vertical max control speed when walking
	private static final float WALKING_SPEED = 4.0f * Units.METERS_PER_SECOND;
	
	// (0; 1], 1 is instant change, 0 is no control authority
	private static final float WALKING_CONTROL_AUTHORITY = 0.1f;
	
	// Vertical velocity instantly add to player when they jump
	private static final float JUMP_VELOCITY = 5f * Units.METERS_PER_SECOND;
	
	private boolean isFlying = true;
	
	private int movementForward = 0;
	private int movementRight = 0;
	private int movementUp = 0;
	
	private double lastSpacePress = Double.NEGATIVE_INFINITY;
	
	private boolean captureMouse = true;
	private boolean useMinecraftGravity = false;
	
	private Runnable updateCallback = null;
	
	public void applyPlayerControls() {
		if (ClientState.getInstance() == null || ClientState.getInstance().getLocalPlayer() == null) {
			return;
		}
		
		EntityData player = getPlayer();
		Mat3 angMat = Matrices.grab3();
		
		angMat.identity().rotateZ(player.getYaw());
		
		Vec3 movement = Vectors.grab3();
		
		movement.set(movementForward, -movementRight, 0);
		
		if (movementForward != 0 && movementRight != 0) {
			movement.normalize();
		}
		
		angMat.mul_(movement); // bug in jglm, .mul() and mul_() are swapped
		
		if (isFlying) {
			movement.z = movementUp;
			movement.mul(FLYING_SPEED);
			movement.sub(player.getVelocity());
			movement.mul(FLYING_CONTROL_AUTHORITY);
		} else {
			movement.mul(WALKING_SPEED);
			movement.sub(player.getVelocity());
			movement.mul(WALKING_CONTROL_AUTHORITY);
			movement.z = 0;
		}
		
		player.getVelocity().add(movement);
		
		Matrices.release(angMat);
		Vectors.release(movement);
	}
	
	public void handleInput(Input input) {
		if (ClientState.getInstance() == null || ClientState.getInstance().getLocalPlayer() == null) {
			return;
		}
		
		InputEvent event = input.getEvent();
		
		if (event instanceof KeyEvent) {
			if (onKeyEvent((KeyEvent) event)) {
				input.consume();
			}
		} else if (event instanceof CursorMoveEvent) {
			onMouseMoved((CursorMoveEvent) event);
			input.consume();
		}
	}

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
			handleSpace(multiplier);
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			handleShift(multiplier);
			break;
			
		case GLFW.GLFW_KEY_ESCAPE:
			if (!event.isPress()) return false;
			handleEscape();
			break;
			
		case GLFW.GLFW_KEY_F5:
			if (!event.isPress()) return false;
			handleCameraMode();
			break;
			
		case GLFW.GLFW_KEY_G:
			if (!event.isPress()) return false;
			handleGravitySwitch();
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

	private void jump() {
		getPlayer().getVelocity().add(0, 0, JUMP_VELOCITY * (useMinecraftGravity ? 2 : 1));
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
	
	private void handleCameraMode() {
		if (ClientState.getInstance().getCamera().hasAnchor()) {
			ClientState.getInstance().getCamera().selectNextMode();
			updateGUI();
		}
	}

	private void handleGravitySwitch() {
		useMinecraftGravity = !useMinecraftGravity;
		updateGUI();
	}

	private void onMouseMoved(CursorMoveEvent event) {
		if (!captureMouse) return;
		
		final float yawScale = -0.002f;
		final float pitchScale = yawScale;

		EntityData player = getPlayer();
		
		normalizeAngles(player.getDirection().add(
				(float) (event.getChangeX() * yawScale),
				(float) (event.getChangeY() * pitchScale)
		));
	}

	private void normalizeAngles(Vec2 dir) {
		// Normalize yaw
		dir.x = FloatMathUtils.normalizeAngle(dir.x);
		
		// Clamp pitch
		dir.y = Glm.clamp(
				dir.y, -FloatMathUtils.PI_F/2, +FloatMathUtils.PI_F/2
		);
	}
	
	private EntityData getPlayer() {
		return ClientState.getInstance().getLocalPlayer();
	}
	
	public void setUpdateCallback(Runnable updateCallback) {
		this.updateCallback = updateCallback;
	}
	
	private void updateGUI() {
		if (this.updateCallback != null) {
			this.updateCallback.run();
		}
	}
	
	public boolean isFlying() {
		return isFlying;
	}
	
	public boolean isMouseCaptured() {
		return captureMouse;
	}
	
	public boolean useMinecraftGravity() {
		return useMinecraftGravity;
	}
	
}
