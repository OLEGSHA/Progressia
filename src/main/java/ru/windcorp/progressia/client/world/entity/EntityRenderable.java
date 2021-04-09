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

package ru.windcorp.progressia.client.world.entity;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.GenericEntity;

public abstract class EntityRenderable implements Renderable, GenericEntity {

	private final EntityData data;
	
	private long stateComputedForFrame = -1;

	public EntityRenderable(EntityData data) {
		this.data = data;
	}

	/**
	 * Updates the state of this model. This method is invoked exactly once per
	 * renderable per frame before this entity is queried for the first time.
	 */
	protected void update() {
		// Do nothing
	}
	
	private void updateIfNecessary() {
		if (stateComputedForFrame != GraphicsInterface.getFramesRendered()) {
			update();
			stateComputedForFrame = GraphicsInterface.getFramesRendered();
		}
	}
	
	@Override
	public final void render(ShapeRenderHelper renderer) {
		updateIfNecessary();
		doRender(renderer);
	}
	
	protected abstract void doRender(ShapeRenderHelper renderer);

	public EntityData getData() {
		return data;
	}

	@Override
	public Vec3 getPosition() {
		return getData().getPosition();
	}

	@Override
	public String getId() {
		return getData().getId();
	}
	
	@Override
	public long getEntityId() {
		return getData().getEntityId();
	}

	public final Vec3 getLookingAt(Vec3 output) {
		if (output == null) output = new Vec3();
		updateIfNecessary();
		doGetLookingAt(output);
		return output;
	}
	
	protected void doGetLookingAt(Vec3 output) {
		output.set(getData().getLookingAt());
	}

	public final Vec3 getUpVector(Vec3 output) {
		if (output == null) output = new Vec3();
		updateIfNecessary();
		doGetUpVector(output);
		return output;
	}
	
	protected void doGetUpVector(Vec3 output) {
		output.set(getData().getUpVector());
	}

	public final Vec3 getViewPoint(Vec3 output) {
		if (output == null) output = new Vec3();
		updateIfNecessary();
		doGetViewPoint(output);
		return output;
	}
	
	protected void doGetViewPoint(Vec3 output) {
		output.set(0, 0, 0);
	}

}
