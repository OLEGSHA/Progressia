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
package ru.windcorp.progressia.server.world.context.impl;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.generic.TileGenericReferenceRO;
import ru.windcorp.progressia.common.world.generic.TileGenericStackRO;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.WorldLogic;

public interface ReusableServerContextBuilders {

	ReusableServerContext build();

	public interface Empty /* does not extend RSCB */ {

		WithWorld in(Server server, WorldLogic world);

		default WithWorld inRealWorldOf(Server server) {
			return in(server, server.getWorld());
		}

	}

	public interface WithWorld extends ReusableServerContextBuilders {

		WithLocation at(Vec3i location);

		default ReusableServerContext at(TileGenericReferenceRO<?, ?, ?, ?, ?> reference) {
			if (!reference.isValid()) {
				throw new IllegalArgumentException("Reference " + reference + " is invalid");
			}

			TileGenericStackRO<?, ?, ?, ?, ?> stack = reference.getStack();
			return at(stack.getBlockInWorld(null)).on(stack.getFace()).index(reference.getIndex());
		}

	}

	public interface WithLocation extends ReusableServerContextBuilders {

		WithTileStack on(RelFace side);
		WithTileStack on(BlockFace side);

		default ReusableServerContext on(TileGenericReferenceRO<?, ?, ?, ?, ?> reference) {
			if (!reference.isValid()) {
				throw new IllegalArgumentException("Reference " + reference + " is invalid");
			}

			TileGenericStackRO<?, ?, ?, ?, ?> stack = reference.getStack();
			return on(stack.getFace()).index(reference.getIndex());
		}

	}

	public interface WithTileStack extends ReusableServerContextBuilders {

		ReusableServerContext index(int index);

		default ReusableServerContext index(TileGenericReferenceRO<?, ?, ?, ?, ?> reference) {
			if (!reference.isValid()) {
				throw new IllegalArgumentException("Reference " + reference + " is invalid");
			}

			return index(reference.getIndex());
		}

	}

}
