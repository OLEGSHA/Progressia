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

package ru.windcorp.progressia.common.world.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import glm.vec._3.i.Vec3i;

public final class BlockFace extends BlockRelation {

	public static final BlockFace TOP = new BlockFace(0, 0, +1, true, "TOP"),
			BOTTOM = new BlockFace(0, 0, -1, false, "BOTTOM"), NORTH = new BlockFace(+1, 0, 0, true, "NORTH"),
			SOUTH = new BlockFace(-1, 0, 0, false, "SOUTH"), WEST = new BlockFace(0, +1, 0, false, "WEST"),
			EAST = new BlockFace(0, -1, 0, true, "EAST");

	private static final ImmutableList<BlockFace> ALL_FACES = ImmutableList.of(TOP, BOTTOM, NORTH, SOUTH, WEST, EAST);

	static {
		link(TOP, BOTTOM);
		link(NORTH, SOUTH);
		link(WEST, EAST);
	}

	private static final ImmutableList<BlockFace> PRIMARY_FACES = ALL_FACES.stream().filter(BlockFace::isPrimary)
			.collect(ImmutableList.toImmutableList());

	private static final ImmutableList<BlockFace> SECONDARY_FACES = ALL_FACES.stream().filter(BlockFace::isSecondary)
			.collect(ImmutableList.toImmutableList());

	public static final int BLOCK_FACE_COUNT = ALL_FACES.size();
	public static final int PRIMARY_BLOCK_FACE_COUNT = PRIMARY_FACES.size();
	public static final int SECONDARY_BLOCK_FACE_COUNT = SECONDARY_FACES.size();

	public static ImmutableList<BlockFace> getFaces() {
		return ALL_FACES;
	}

	public static ImmutableList<BlockFace> getPrimaryFaces() {
		return PRIMARY_FACES;
	}

	public static ImmutableList<BlockFace> getSecondaryFaces() {
		return SECONDARY_FACES;
	}

	private static void link(BlockFace a, BlockFace b) {
		a.counterFace = b;
		b.counterFace = a;
	}

	public static <E> ImmutableMap<BlockFace, E> mapToFaces(E top, E bottom, E north, E south, E east, E west) {
		return ImmutableMap.<BlockFace, E>builderWithExpectedSize(6).put(TOP, top).put(BOTTOM, bottom).put(NORTH, north)
				.put(SOUTH, south).put(EAST, east).put(WEST, west).build();
	}

	private static int nextId = 0;

	private final int id;
	private final String name;
	private BlockFace counterFace;
	private final boolean isPrimary;

	private BlockFace(int x, int y, int z, boolean isPrimary, String name) {
		super(x, y, z);
		this.id = nextId++;
		this.isPrimary = isPrimary;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public BlockFace getPrimary() {
		if (isPrimary)
			return this;
		else
			return counterFace;
	}

	public BlockFace getPrimaryAndMoveCursor(Vec3i cursor) {
		if (isPrimary)
			return this;

		cursor.add(getVector());
		return counterFace;
	}

	public boolean isSecondary() {
		return !isPrimary;
	}

	public BlockFace getSecondary() {
		if (isPrimary)
			return counterFace;
		else
			return this;
	}

	public BlockFace getSecondaryAndMoveCursor(Vec3i cursor) {
		if (!isPrimary)
			return this;

		cursor.add(getVector());
		return counterFace;
	}

	public BlockFace getCounter() {
		return counterFace;
	}

	public BlockFace getCounterAndMoveCursor(Vec3i cursor) {
		cursor.add(getVector());
		return counterFace;
	}

	public int getId() {
		return id;
	}

	@Override
	public float getEuclideanDistance() {
		return 1.0f;
	}

	@Override
	public int getChebyshevDistance() {
		return 1;
	}

	@Override
	public int getManhattanDistance() {
		return 1;
	}

	@Override
	public String toString() {
		return getName();
	}

}
