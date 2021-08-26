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

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.EntityDataRegistry;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerBlockContextRO;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;

public class TestBlockLogicStatieSpawner extends BlockLogic implements TickableBlock {

	public TestBlockLogicStatieSpawner(String id) {
		super(id);
	}

	@Override
	public void tick(ServerBlockContext context) {
		Vec3i loc = context.toAbsolute(context.getLocation(), null);
		EntityData entity = EntityDataRegistry.getInstance().create("Test:Statie");
		entity.setPosition(new Vec3(loc.x, loc.y, loc.z));
		
		context.addEntity(entity);
		context.setBlock(BlockDataRegistry.getInstance().get("Test:Air"));
	}
	
	@Override
	public TickingPolicy getTickingPolicy(ServerBlockContextRO context) {
		return TickingPolicy.RANDOM;
	}

}
