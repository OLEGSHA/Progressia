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

package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.PacketAffectBlock;

public class CachedBlockChange<P extends PacketAffectBlock> extends CachedChunkChange<P> {

	public CachedBlockChange(Consumer<? super CachedChange> disposer, P packet) {
		super(disposer, packet);
	}

	@Override
	public int hashCode() {
		PacketAffectBlock packet = getPacket();
		Vec3i biw = packet.getBlockInWorld();

		final int prime = 31;
		int result = 1;
		result = prime * result + biw.x;
		result = prime * result + biw.y;
		result = prime * result + biw.z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass())
			return false;

		PacketAffectBlock my = getPacket();
		PacketAffectBlock other = ((CachedBlockChange<?>) obj).getPacket();

		return Glm.equals(my.getBlockInWorld(), other.getBlockInWorld());
	}

	@Override
	public String toString() {
		Vec3i biw = getPacket().getBlockInWorld();

		return getClass().getSimpleName() + " (" + biw.x + "; " + biw.y + "; " + biw.z + ")";
	}

}
