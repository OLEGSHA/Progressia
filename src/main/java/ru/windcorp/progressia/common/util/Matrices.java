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

import glm.mat._3.Mat3;
import glm.mat._4.Mat4;
import glm.mat._4.d.Mat4d;

/**
 * A set of caches for GLM matrix objects.
 * <p>
 * All {@code grab}bed objects must be {@code release}d as soon as possible.
 * Ideally, user code should be:
 * 
 * <pre>
 * Mat4 myMatrix = Vectors.grab4();
 * try {
 * 	// use myMatrix
 * } finally {
 * 	Matrices.release(myMatrix);
 * }
 * </pre>
 * 
 * Provided objects may be reused after {@code release} has been invoked; do not
 * store them.
 * <p>
 * This class is thread- and recursion-safe.
 * 
 * @see Vectors
 */
public class Matrices {

	private static final LowOverheadCache<Mat3> MAT3S = new LowOverheadCache<>(Mat3::new);

	public static Mat3 grab3() {
		return MAT3S.grab();
	}

	public static void release(Mat3 m) {
		MAT3S.release(m);
	}

	private static final LowOverheadCache<Mat4> MAT4S = new LowOverheadCache<>(Mat4::new);

	public static Mat4 grab4() {
		return MAT4S.grab();
	}

	public static void release(Mat4 m) {
		MAT4S.release(m);
	}

	private static final LowOverheadCache<Mat4d> MAT4DS = new LowOverheadCache<>(Mat4d::new);

	public static Mat4d grab4d() {
		return MAT4DS.grab();
	}

	public static void release(Mat4d m) {
		MAT4DS.release(m);
	}

	private Matrices() {
	}

}
