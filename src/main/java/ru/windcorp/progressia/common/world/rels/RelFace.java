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
package ru.windcorp.progressia.common.world.rels;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import glm.vec._3.i.Vec3i;

public class RelFace extends RelRelation {

	// @formatter:off
	public static final RelFace
		UP    = new RelFace( 0,  0, +1, "UP"),
		DOWN  = new RelFace( 0,  0, -1, "DOWN"),
		NORTH = new RelFace(+1,  0,  0, "NORTH"),
		SOUTH = new RelFace(-1,  0,  0, "SOUTH"),
		WEST  = new RelFace( 0, +1,  0, "WEST"),
		EAST  = new RelFace( 0, -1,  0, "EAST");
	// @formatter:on

	private static final ImmutableList<RelFace> ALL_FACES = ImmutableList.of(UP, DOWN, NORTH, SOUTH, WEST, EAST);

	static {
		link(UP, DOWN);
		link(NORTH, SOUTH);
		link(WEST, EAST);
	}

	public static ImmutableList<RelFace> getFaces() {
		return ALL_FACES;
	}

	private static void link(RelFace a, RelFace b) {
		a.counterFace = b;
		b.counterFace = a;
	}

	public static <E> ImmutableMap<RelFace, E> mapToFaces(
		E up,
		E down,
		E north,
		E south,
		E west,
		E east
	) {
		return ImmutableMap.<RelFace, E>builderWithExpectedSize(6)
			.put(UP, up)
			.put(DOWN, down)
			.put(NORTH, north)
			.put(SOUTH, south)
			.put(WEST, west)
			.put(EAST, east)
			.build();
	}

	private static int nextId = 0;

	private final int id;
	private final String name;
	private RelFace counterFace;

	private RelFace(int x, int y, int z, String name) {
		super(x, y, z, true);
		this.id = nextId++;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public RelFace getCounter() {
		return counterFace;
	}

	public RelFace getCounterAndMoveCursor(Vec3i cursor) {
		cursor.add(getVector());
		return counterFace;
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
