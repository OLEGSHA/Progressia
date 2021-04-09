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

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;

public final class AbsFace extends AbsRelation implements BlockFace {

	// @formatter:off
	public static final AbsFace
		POS_Z = new AbsFace( 0,  0, +1,  true, "POS_Z"),
		NEG_Z = new AbsFace( 0,  0, -1, false, "NEG_Z"),
		POS_X = new AbsFace(+1,  0,  0,  true, "POS_X"),
		NEG_X = new AbsFace(-1,  0,  0, false, "NEG_X"),
		POS_Y = new AbsFace( 0, +1,  0, false, "POS_Y"),
		NEG_Y = new AbsFace( 0, -1,  0,  true, "NEG_Y");
	// @formatter:on

	private static final ImmutableList<AbsFace> ALL_FACES = ImmutableList.of(POS_Z, NEG_Z, POS_X, NEG_X, POS_Y, NEG_Y);

	static {
		link(POS_Z, NEG_Z);
		link(POS_X, NEG_X);
		link(POS_Y, NEG_Y);
	}

	private static final ImmutableList<AbsFace> PRIMARY_FACES = ALL_FACES.stream().filter(AbsFace::isPrimary)
		.collect(ImmutableList.toImmutableList());

	private static final ImmutableList<AbsFace> SECONDARY_FACES = ALL_FACES.stream().filter(AbsFace::isSecondary)
		.collect(ImmutableList.toImmutableList());

	public static final int PRIMARY_BLOCK_FACE_COUNT = PRIMARY_FACES.size();
	public static final int SECONDARY_BLOCK_FACE_COUNT = SECONDARY_FACES.size();

	public static ImmutableList<AbsFace> getFaces() {
		return ALL_FACES;
	}

	public static ImmutableList<AbsFace> getPrimaryFaces() {
		return PRIMARY_FACES;
	}

	public static ImmutableList<AbsFace> getSecondaryFaces() {
		return SECONDARY_FACES;
	}

	private static void link(AbsFace a, AbsFace b) {
		a.counterFace = b;
		b.counterFace = a;
	}

	public static <E> ImmutableMap<AbsFace, E> mapToFaces(
		E posZ,
		E negZ,
		E posX,
		E negX,
		E negY,
		E posY
	) {
		return ImmutableMap.<AbsFace, E>builderWithExpectedSize(6)
			.put(POS_Z, posZ)
			.put(NEG_Z, negZ)
			.put(POS_X, posX)
			.put(NEG_X, negX)
			.put(NEG_Y, negY)
			.put(POS_Y, posY)
			.build();
	}
	
	public static <E> ImmutableMap<AbsFace, E> mapToFaces(Function<AbsFace, E> generator) {
		return mapToFaces(
			generator.apply(POS_Z),
			generator.apply(NEG_Z),
			generator.apply(POS_X),
			generator.apply(NEG_X),
			generator.apply(NEG_Y),
			generator.apply(POS_Y)
		);
	}
	
	/**
	 * Rounds the provided vector to one of {@link AbsFace}s. The returned face
	 * is pointing in the same general direction as the provided vector. The
	 * result is undefined for arguments where two largest in absolute values
	 * coordinates are equal (e.g. for {@code (5; -5; 2)}). For a zero vector
	 * the result is {@code null}. Infinite vectors are handled correctly.
	 * 
	 * @param vector the vector to round
	 * @return the face most adequately describing the provided vector, or
	 *         {@code null} iff {@code vector.x = vector.y = vector.z = 0}
	 * @throws IllegalArgumentException if one of the coordinates is a NaN
	 */
	public static AbsFace roundToFace(Vec3 vector) {
		Objects.requireNonNull(vector, "vector");
		return roundToFace(vector.x, vector.y, vector.z);
	}
	
	/**
	 * Rounds the provided vector to one of {@link AbsFace}s. The returned face
	 * is pointing in the same general direction as the provided vector. The
	 * result is undefined for arguments where two largest in absolute values
	 * coordinates are equal (e.g. for {@code (5; -5; 2)}). For a zero vector
	 * the result is {@code null}. Infinite arguments are handled correctly.
	 * 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return the face most adequately describing the provided vector, or
	 *         {@code null} iff {@code x = y = z = 0}
	 * @throws IllegalArgumentException if one of the coordinates is a NaN
	 */
	public static AbsFace roundToFace(float x, float y, float z) {
		if (x == 0 && y == 0 && z == 0) {
			return null;
		}
		
		if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z)) {
			throw new IllegalArgumentException("Vector contains NaN: (" + x + "; " + y + "; " + z + ")");
		}
		
		// The following code handles infinite x, y or z properly
		
		float absX = Math.abs(x);
		float absY = Math.abs(y);
		float absZ = Math.abs(z);
		
		if (absX > absY) {
			if (absX > absZ) {
				return x > 0 ? POS_X : NEG_X;
			} else {
				// Z is the answer; exit decision tree
			}
		} else {
			if (absY > absZ) {
				return y > 0 ? POS_Y : NEG_Y;
			} else {
				// Z is the answer; exit decision tree
			}
		}
		
		return z > 0 ? POS_Z : NEG_Z;
	}
	
	/**
	 * Rounds the provided vector to one of {@link AbsFace}s. The returned face
	 * is pointing in the same general direction as the provided vector. The
	 * result is undefined for arguments where two largest in absolute values
	 * coordinates are equal (e.g. for {@code (5; -5; 2)}). For a zero vector
	 * the result is {@code null}.
	 * 
	 * @param vector the vector to round
	 * @return the face most adequately describing the provided vector, or
	 *         {@code null} iff {@code vector.x = vector.y = vector.z = 0}
	 */
	public static AbsFace roundToFace(Vec3i vector) {
		Objects.requireNonNull(vector, "vector");
		return roundToFace(vector.x, vector.y, vector.z);
	}
	
	/**
	 * Rounds the provided vector to one of {@link AbsFace}s. The returned face
	 * is pointing in the same general direction as the provided vector. The
	 * result is undefined for arguments where two largest in absolute values
	 * coordinates are equal (e.g. for {@code (5; -5; 2)}). For a zero vector
	 * the result is {@code null}.
	 * 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return the face most adequately describing the provided vector, or
	 *         {@code null} iff {@code x = y = z = 0}
	 */
	public static AbsFace roundToFace(int x, int y, int z) {
		if (x == 0 && y == 0 && z == 0) {
			return null;
		}
		
		int absX = Math.abs(x);
		int absY = Math.abs(y);
		int absZ = Math.abs(z);
		
		if (absX > absY) {
			if (absX > absZ) {
				return x > 0 ? POS_X : NEG_X;
			} else {
				// Z is the answer; exit decision tree
			}
		} else {
			if (absY > absZ) {
				return y > 0 ? POS_Y : NEG_Y;
			} else {
				// Z is the answer; exit decision tree
			}
		}
		
		return z > 0 ? POS_Z : NEG_Z;
	}

	private static int nextId = 0;

	private final int id;
	private final String name;
	private AbsFace counterFace;
	private final boolean isPrimary;

	private AbsFace(int x, int y, int z, boolean isPrimary, String name) {
		super(x, y, z);
		this.id = nextId++;
		this.isPrimary = isPrimary;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public AbsFace resolve(AbsFace up) {
		return this;
	}
	
	@Override
	public RelFace relativize(AbsFace up) {
		return BlockFaceResolver.relativize(this, up);
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public AbsFace getPrimary() {
		if (isPrimary)
			return this;
		else
			return counterFace;
	}

	public AbsFace getPrimaryAndMoveCursor(Vec3i cursor) {
		if (isPrimary)
			return this;

		cursor.add(getVector());
		return counterFace;
	}

	public boolean isSecondary() {
		return !isPrimary;
	}

	public AbsFace getSecondary() {
		if (isPrimary)
			return counterFace;
		else
			return this;
	}

	public AbsFace getSecondaryAndMoveCursor(Vec3i cursor) {
		if (!isPrimary)
			return this;

		cursor.add(getVector());
		return counterFace;
	}

	public AbsFace getCounter() {
		return counterFace;
	}

	public AbsFace getCounterAndMoveCursor(Vec3i cursor) {
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
