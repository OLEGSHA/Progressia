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

package ru.windcorp.progressia.client.graphics.world;

import java.util.ArrayList;
import java.util.List;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.comms.controls.InputBasedControls;
import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.backend.FaceCulling;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.model.Shapes.PppBuilder;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.colliders.Collider;
import ru.windcorp.progressia.common.util.FloatMathUtil;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.test.CollisionModelRenderer;
import ru.windcorp.progressia.test.TestPlayerControls;

public class LayerWorld extends Layer {

	private final WorldRenderHelper helper = new WorldRenderHelper();

	private final Client client;
	private final InputBasedControls inputBasedControls;
	private final TestPlayerControls tmp_testControls = TestPlayerControls.getInstance();

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
		Camera camera = client.getCamera();
		if (camera.hasAnchor()) {
			renderWorld();
		}

		client.getLocalPlayer().getEntity();

		if (client.isReady()) {
			client.getLocalPlayer().update(client.getWorld());
		}
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

	private static final boolean RENDER_COLLISION_MODELS = false;

	private void tmp_doEveryFrame() {
		float tickLength = (float) GraphicsInterface.getFrameLength();

		try {
			tmp_performCollisions(tickLength);
			tmp_drawSelectionBox();

			tmp_testControls.applyPlayerControls();

			for (EntityData data : this.client.getWorld().getData().getEntities()) {
				tmp_applyFriction(data, tickLength);
				tmp_applyGravity(data, tickLength);
				tmp_renderCollisionModel(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("OLEGSHA is to blame. Tell him he vry stupiDD!!");
			System.exit(31337);
		}
	}

	private void tmp_renderCollisionModel(EntityData entity) {
		if (RENDER_COLLISION_MODELS) {
			CollisionModelRenderer.renderCollisionModel(entity.getCollisionModel(), helper);
		}
	}

	private void tmp_performCollisions(float tickLength) {
		tmp_collideableList.clear();
		tmp_collideableList.addAll(this.client.getWorld().getData().getEntities());

		Collider.performCollisions(tmp_collideableList, this.client.getWorld().getData(), tickLength,
				tmp_colliderWorkspace);
	}

	private static final Renderable SELECTION_BOX = tmp_createSelectionBox();

	private void tmp_drawSelectionBox() {
		if (!client.isReady())
			return;

		Vec3i selection = client.getLocalPlayer().getSelection().getBlock();
		if (selection == null)
			return;

		helper.pushTransform().translate(selection.x, selection.y, selection.z);
		SELECTION_BOX.render(helper);
		helper.popTransform();
	}

	private static Renderable tmp_createSelectionBox() {
		StaticModel.Builder b = StaticModel.builder();
		ShapeRenderProgram p = WorldRenderProgram.getDefault();

		final float f = 1e-2f;
		final float scale = 1 - f / 2;
		final Vec4 color = new Vec4(0, 0, 0, 1);

		for (float phi = 0; phi < 2 * FloatMathUtil.PI_F; phi += FloatMathUtil.PI_F / 2) {
			Mat4 rot = new Mat4().identity().rotateZ(phi).scale(scale);

			b.addPart(new PppBuilder(p, (Texture) null).setOrigin(new Vec3(-f - 0.5f, -f - 0.5f, -f - 0.5f))
					.setSize(f, f, 2 * f + 1).setColorMultiplier(color).create(), rot);

			b.addPart(new PppBuilder(p, (Texture) null).setOrigin(new Vec3(-f - 0.5f, -0.5f, -f - 0.5f))
					.setSize(f, 1, f).setColorMultiplier(color).create(), rot);

			b.addPart(new PppBuilder(p, (Texture) null).setOrigin(new Vec3(-f - 0.5f, -0.5f, +0.5f)).setSize(f, 1, f)
					.setColorMultiplier(color).create(), rot);
		}

		return b.build();
	}

	private static final float FRICTION_COEFF = Units.get("1e-5f kg/s");

	private void tmp_applyFriction(EntityData entity, float tickLength) {
		entity.getVelocity().mul((float) Math.exp(-FRICTION_COEFF / entity.getCollisionMass() * tickLength));
	}

	private static final float MC_g = Units.get("32  m/s^2");
	private static final float IRL_g = Units.get("9.8 m/s^2");

	private void tmp_applyGravity(EntityData entity, float tickLength) {
		if (ClientState.getInstance().getLocalPlayer().getEntity() == entity && tmp_testControls.isFlying()) {
			return;
		}

		final float gravitationalAcceleration = tmp_testControls.useMinecraftGravity() ? MC_g : IRL_g;
		entity.getVelocity().add(0, 0, -gravitationalAcceleration * tickLength);
	}

	@Override
	protected void handleInput(Input input) {
		if (input.isConsumed())
			return;

		tmp_testControls.handleInput(input);

		if (!input.isConsumed()) {
			inputBasedControls.handleInput(input);
		}
	}

}
