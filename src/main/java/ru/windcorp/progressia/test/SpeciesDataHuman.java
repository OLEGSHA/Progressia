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
package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.world.entity.SpeciesData;

public class SpeciesDataHuman extends SpeciesData {
	
	public static final float HEIGHT = Units.get("180 cm");
	public static final float WIDTH = Units.get("80 cm");

	public SpeciesDataHuman(String id) {
		super(id);
		
		withHands(new Hand("Right"), new Hand("Left"));
		withEquipmentSlots(/* ._. nope */);
	}

	@Override
	public CollisionModel getCollisionModel() {
		return new AABB(0, 0, HEIGHT / 2, WIDTH, WIDTH, HEIGHT);
	}

}
