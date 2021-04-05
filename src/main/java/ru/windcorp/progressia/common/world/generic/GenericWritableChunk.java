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
package ru.windcorp.progressia.common.world.generic;

import glm.vec._3.i.Vec3i;

// @formatter:off
public interface GenericWritableChunk<
	B  extends GenericBlock,
	T  extends GenericTile,
	TS extends GenericWritableTileStack <B, T, TS, TR, C>,
	TR extends GenericTileReference     <B, T, TS, TR, C>,
	C  extends GenericWritableChunk     <B, T, TS, TR, C>
>
	extends GenericChunk<B, T, TS, TR, C> {
// @formatter:on

	void setBlock(Vec3i posInChunk, B block, boolean notify);

	void setBlockRel(Vec3i relativeBlockInChunk, B block, boolean notify);

}
