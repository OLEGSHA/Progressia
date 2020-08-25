/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.common.block;

import com.google.common.collect.ImmutableList;

public final class BlockFace extends BlockRelation {
	
	public static final BlockFace
			TOP    = new BlockFace( 0,  0, +1, true),
			BOTTOM = new BlockFace( 0,  0, -1, false),
			NORTH  = new BlockFace(+1,  0,  0, true),
			SOUTH  = new BlockFace(-1,  0,  0, false),
			WEST   = new BlockFace( 0, +1,  0, true),
			EAST   = new BlockFace( 0, -1,  0, false);
	
	private static final ImmutableList<BlockFace> ALL_VALUES =
			ImmutableList.of(TOP, BOTTOM, NORTH, SOUTH, WEST, EAST);
	
	public static ImmutableList<BlockFace> getValues() {
		return ALL_VALUES;
	}
	
	static {
		link(TOP, BOTTOM);
		link(NORTH, SOUTH);
		link(WEST, EAST);
	}
	
	private static void link(BlockFace a, BlockFace b) {
		a.counterFace = b;
		b.counterFace = a;
	}
	
	private BlockFace counterFace;
	private final boolean isPrimary;

	private BlockFace(int x, int y, int z, boolean isPrimary) {
		super(x, y, z);
		this.isPrimary = isPrimary;
	}
	
	public boolean isPrimary() {
		return isPrimary;
	}
	
	public BlockFace getPrimary() {
		if (isPrimary) return this;
		else return counterFace;
	}
	
	public BlockFace getSecondary() {
		if (isPrimary) return counterFace;
		else return this;
	}

}
