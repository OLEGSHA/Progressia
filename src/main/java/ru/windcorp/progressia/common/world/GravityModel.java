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
package ru.windcorp.progressia.common.world;

import java.util.Objects;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;

/**
 * Gravity model specifies the gravitational acceleration field. A gravity model
 * may be queried for the vector of gravitational acceleration that should
 * affect an object. This vector is, generally speaking, a function of space:
 * gravity in two different locations may vary. Gravity may also be a zero
 * vector.
 * 
 * @author javapony
 */
public abstract class GravityModel extends Namespaced {

	public GravityModel(String id) {
		super(id);
	}

	/**
	 * Computes the vector of gravitational acceleration at the provided
	 * location.
	 * 
	 * @param pos    the position to compute gravity at
	 * @param output a {@link Vec3} where the result is stored. May be
	 *               {@code null}.
	 * @return the vector of gravitational acceleration. The returned object
	 *         will match {@code output} parameter is it is non-null.
	 */
	public Vec3 getGravity(Vec3 pos, Vec3 output) {
		Objects.requireNonNull(pos, "pos");

		if (output == null) {
			output = new Vec3();
		}

		try {
			doGetGravity(pos, output);
		} catch (Exception e) {
			throw CrashReports.report(e, "%s failed to compute gravity at (%d; %d; %d)", this, pos.x, pos.y, pos.z);
		}

		return output;
	}

	/**
	 * Computes the up direction at the provided location. Up vector is defined
	 * as the additive inverse of the normalized gravitational acceleration
	 * vector or {@code (0; 0; 0)} if there is no gravity.
	 * 
	 * @param pos    the position to compute up vector at
	 * @param output a {@link Vec3} where the result is stored. May be
	 *               {@code null}.
	 * @return the up vector. The returned object will match {@code output}
	 *         parameter is it is non-null.
	 */
	public Vec3 getUp(Vec3 pos, Vec3 output) {
		output = getGravity(pos, output);
		if (output.any())
			output.normalize().negate();
		return output;
	}

	/**
	 * Computes the gravitational acceleration vector at the provided location.
	 * Actual computation of gravity is delegated to this method by the other
	 * methods in this class.
	 * 
	 * @param pos    the position to compute gravity at
	 * @param output a {@link Vec3} where the result must be stored. Never
	 *               {@code null}.
	 */
	protected abstract void doGetGravity(Vec3 pos, Vec3 output);

}
