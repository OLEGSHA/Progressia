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

package ru.windcorp.progressia.common.util;

import glm.vec._2.Vec2;
import glm.vec._2.i.Vec2i;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import glm.vec._4.i.Vec4i;

/**
 * A set of caches for GLM vector objects.
 * <p>
 * All {@code grab}bed objects must be {@code release}d as soon as possible.
 * Ideally, user code should be:
 * 
 * <pre>
 * Vec3 myVector = Vectors.grab3();
 * try {
 * 	// use myVector
 * } finally {
 * 	Vectors.release(myVector);
 * }
 * </pre>
 * 
 * Provided objects may be reused after {@code release} has been invoked; do not
 * store them.
 * <p>
 * This class is thread- and recursion-safe.
 * 
 * @see Matrices
 */
public class Vectors {

	public static final Vec2 ZERO_2 = new Vec2(0, 0);
	public static final Vec2i ZERO_2i = new Vec2i(0, 0);
	public static final Vec3 ZERO_3 = new Vec3(0, 0, 0);
	public static final Vec3i ZERO_3i = new Vec3i(0, 0, 0);
	public static final Vec4 ZERO_4 = new Vec4(0, 0, 0, 0);
	public static final Vec4i ZERO_4i = new Vec4i(0, 0, 0, 0);
	public static final Vec2 UNIT_2 = new Vec2(1, 1);
	public static final Vec2i UNIT_2i = new Vec2i(1, 1);
	public static final Vec3 UNIT_3 = new Vec3(1, 1, 1);
	public static final Vec3i UNIT_3i = new Vec3i(1, 1, 1);
	public static final Vec4 UNIT_4 = new Vec4(1, 1, 1, 1);
	public static final Vec4i UNIT_4i = new Vec4i(1, 1, 1, 1);

	private static final LowOverheadCache<Vec3i> VEC3IS = new LowOverheadCache<>(Vec3i::new);

	public static Vec3i grab3i() {
		return VEC3IS.grab();
	}

	public static void release(Vec3i v) {
		VEC3IS.release(v);
	}

	private static final LowOverheadCache<Vec3> VEC3S = new LowOverheadCache<>(Vec3::new);

	public static Vec3 grab3() {
		return VEC3S.grab();
	}

	public static void release(Vec3 v) {
		VEC3S.release(v);
	}

	private static final LowOverheadCache<Vec2i> VEC2IS = new LowOverheadCache<>(Vec2i::new);

	public static Vec2i grab2i() {
		return VEC2IS.grab();
	}

	public static void release(Vec2i v) {
		VEC2IS.release(v);
	}

	private static final LowOverheadCache<Vec2> VEC2S = new LowOverheadCache<>(Vec2::new);

	public static Vec2 grab2() {
		return VEC2S.grab();
	}

	public static void release(Vec2 v) {
		VEC2S.release(v);
	}

	private static final LowOverheadCache<Vec4i> VEC4IS = new LowOverheadCache<>(Vec4i::new);

	public static Vec4i grab4i() {
		return VEC4IS.grab();
	}

	public static void release(Vec4i v) {
		VEC4IS.release(v);
	}

	private static final LowOverheadCache<Vec4> VEC4S = new LowOverheadCache<>(Vec4::new);

	public static Vec4 grab4() {
		return VEC4S.grab();
	}

	public static void release(Vec4 v) {
		VEC4S.release(v);
	}

	private Vectors() {
	}

}
