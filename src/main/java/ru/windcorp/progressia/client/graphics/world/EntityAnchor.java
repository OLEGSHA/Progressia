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

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.world.Camera.Anchor;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class EntityAnchor implements Anchor {

	private final EntityData entity;
	private final EntityRenderable model;

	private final Collection<Mode> modes;

	public EntityAnchor(EntityRenderable model) {
		this.entity = model.getData();
		this.model = model;

		this.modes = ImmutableList.of(
				// From viewpoint / first person
				Mode.of(v -> v.set(0), m -> {
				}),

				// Third person, looking forward
				Mode.of(v -> v.set(-3.5f, +0.5f, 0), m -> {
				}),

				// Third person, looking back
				Mode.of(v -> v.set(-3.5f, 0, 0), m -> m.rotateZ((float) Math.PI)));
	}

	@Override
	public void getCameraPosition(Vec3 output) {
		model.getViewPoint(output);
		output.add(entity.getPosition());
	}

	@Override
	public void getCameraVelocity(Vec3 output) {
		output.set(entity.getVelocity());
	}

	@Override
	public float getCameraYaw() {
		return entity.getYaw();
	}

	@Override
	public float getCameraPitch() {
		return entity.getPitch();
	}

	@Override
	public Collection<Mode> getCameraModes() {
		return modes;
	}

}
