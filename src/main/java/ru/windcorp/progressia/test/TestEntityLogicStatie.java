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

import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.world.TickContext;
import ru.windcorp.progressia.server.world.entity.EntityLogic;

public class TestEntityLogicStatie extends EntityLogic {

	public TestEntityLogicStatie(String id) {
		super(id);
	}

	@Override
	public void tick(EntityData entity, TickContext context) {
		super.tick(entity, context);

		TestEntityDataStatie statie = (TestEntityDataStatie) entity;

		int size = (int) (18 + 6 * Math.sin(entity.getAge()));
		context.getServer().getWorldAccessor().changeEntity(statie, e -> e.setSizeNow(size));
	}

}
