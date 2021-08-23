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

package ru.windcorp.progressia.common.collision;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import glm.vec._3.Vec3;

public class CompoundCollisionModel implements CollisionModel {

	private final Collection<? extends CollisionModel> models;

	public CompoundCollisionModel(Collection<? extends CollisionModel> models) {
		this.models = models;
	}

	public CompoundCollisionModel(CollisionModel... models) {
		this(ImmutableList.copyOf(models));
	}

	public Collection<? extends CollisionModel> getModels() {
		return models;
	}

	@Override
	public void setOrigin(Vec3 origin) {
		for (CollisionModel model : getModels()) {
			model.setOrigin(origin);
		}
	}

	@Override
	public void moveOrigin(Vec3 displacement) {
		for (CollisionModel model : getModels()) {
			model.moveOrigin(displacement);
		}
	}

}
