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

import java.util.ArrayList;
import java.util.Collection;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.LowOverheadCache;
import ru.windcorp.progressia.common.world.WorldData;

public class WorldCollisionHelper {

	private final Collideable collideable = new Collideable() {
		@Override
		public boolean onCollision(Collideable other) {
			return false;
		}

		@Override
		public void moveAsCollideable(Vec3 displacement) {
			// Ignore
			assert displacement.length() < 1e-3f;
		}

		@Override
		public CollisionModel getCollisionModel() {
			return WorldCollisionHelper.this.model;
		}

		@Override
		public float getCollisionMass() {
			return Float.POSITIVE_INFINITY;
		}

		@Override
		public void getCollideableVelocity(Vec3 output) {
			output.set(0);
		}

		@Override
		public void changeVelocityOnCollision(Vec3 velocityChange) {
			// Ignore
			assert velocityChange.length() < 1e-3f;
		}
	};

	private final Collection<TranslatedAABB> activeBlockModels = new ArrayList<>();
	private final CollisionModel model = new CompoundCollisionModel(activeBlockModels);
	private final LowOverheadCache<TranslatedAABB> blockModelCache = new LowOverheadCache<>(TranslatedAABB::new);

	/**
	 * Changes the state of this helper's {@link #getCollideable()} so it is
	 * ready to adequately handle collisions with the {@code collideable} that
	 * might happen in the next {@code maxTime} seconds. This helper is only
	 * valid for checking collisions with the given Collideable and only within
	 * the given time limit.
	 * 
	 * @param collideable
	 *            the {@link Collideable} that collisions will be checked
	 *            against
	 * @param maxTime
	 *            maximum collision time
	 */
	public void tuneToCollideable(WorldData world, Collideable collideable, float maxTime) {
		activeBlockModels.forEach(blockModelCache::release);
		activeBlockModels.clear();
		CollisionPathComputer.forEveryBlockInCollisionPath(collideable, maxTime,
				v -> addModel(world.getCollisionModelOfBlock(v), v));
	}

	private void addModel(CollisionModel model, Vec3i pos) {
		if (model == null) {
			// Ignore
		} else if (model instanceof AABBoid) {
			addAABBoidModel((AABBoid) model, pos);
		} else if (model instanceof CompoundCollisionModel) {
			for (CollisionModel subModel : ((CompoundCollisionModel) model).getModels()) {
				addModel(subModel, pos);
			}
		} else {
			throw new RuntimeException("not supported");
		}
	}

	private void addAABBoidModel(AABBoid model, Vec3i pos) {
		TranslatedAABB translator = blockModelCache.grab();
		translator.setParent(model);
		translator.setTranslation(pos.x, pos.y, pos.z);
		activeBlockModels.add(translator);
	}

	public Collideable getCollideable() {
		return collideable;
	}

}
