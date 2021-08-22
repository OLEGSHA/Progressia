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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.rels.AbsFace;

/**
 * Gravity model specifies the gravitational acceleration field, the up
 * direction field and the discrete up direction field.
 * <p>
 * A gravity model may be queried for the vector of gravitational acceleration
 * that should affect an object. This vector is, generally speaking, a function
 * of space: gravity in two different locations may vary. Gravity may also be a
 * zero vector.
 * <p>
 * The vector of gravitational acceleration defines the up direction. Up vector
 * is defined as the additive inverse of the normalized gravitational
 * acceleration vector or {@code (0; 0; 0)} if there is no gravity.
 * <p>
 * Separately from the gravitational acceleration and the up vectors, a
 * <em>discrete up</em> vector field is specified by a gravity model. This field
 * is defined for each chunk uniquely and may only take the value of one of the
 * six {@linkplain AbsFace absolute directions}. This vector specifies the
 * rotation of blocks, tiles and other objects that may not have a
 * non-axis-aligned direction. Discrete up vector must be specified even for
 * chunks that have a zero or an ambiguous up direction. Although discrete up
 * direction is not technically linked to the up direction, is it expected by
 * the players that they generally align.
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

		if (output.any()) {
			output.normalize().negate();
		}

		return output;
	}

	/**
	 * Computes the discrete up vector for the chunk at the specified
	 * coordinates.
	 * 
	 * @param chunkPos the coordinates of chunk to compute discrete up at
	 * @return an {@link AbsFace} that corresponds to the up direction in the
	 *         specified chunk. Never {@code null}.
	 */
	public AbsFace getDiscreteUp(Vec3i chunkPos) {
		Objects.requireNonNull(chunkPos, "chunkPos");

		final AbsFace result;

		try {
			result = doGetDiscreteUp(chunkPos);
		} catch (Exception e) {
			throw CrashReports.report(
				e,
				"%s failed to compute discrete up at (%d; %d; %d)",
				this,
				chunkPos.x,
				chunkPos.y,
				chunkPos.z
			);
		}

		if (result == null) {
			throw CrashReports.report(
				null,
				"%s has computed null as the discrete up at (%d; %d; %d). This is forbidden.",
				this,
				chunkPos.x,
				chunkPos.y,
				chunkPos.z
			);
		}

		return result;
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

	/**
	 * Computes the discrete up vector for the chunk at the specified
	 * coordinates. A direction must be assigned under any circumstances. Actual
	 * computation of discrete up is delegated to this method by the other
	 * methods in this class.
	 * 
	 * @param chunkPos the coordinates of chunk to compute discrete up at
	 * @return an {@link AbsFace} that corresponds to the up direction in the
	 *         specified chunk. Never {@code null}.
	 */
	protected abstract AbsFace doGetDiscreteUp(Vec3i chunkPos);
	
	/**
	 * Parses the settings from the provided {@link DataInput} and configures this object appropriately. This method will not necessarily exhaust the input.
	 * @param input a stream to read the settings from
	 * @throws IOException if an I/O error occurs
	 * @throws DecodingException if the settings could not be parsed from input
	 */
	public void readSettings(DataInput input) throws IOException, DecodingException {
		Objects.requireNonNull(input, "input");
		
		try {
			doReadSettings(input);
		} catch (IOException | DecodingException e) {
			throw e;
		} catch (Exception e) {
			throw CrashReports.report(
				e,
				"%s failed to read its settings",
				this
			);
		}
	}
	
	/**
	 * Encodes the settings of this model into the provided {@link DataOutput}.
	 * @param output a stream to write the settings into
	 * @throws IOException if an I/O error occurs
	 */
	public void writeSettings(DataOutput output) throws IOException {
		Objects.requireNonNull(output, "output");
		
		try {
			doWriteSettings(output);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw CrashReports.report(
				e,
				"%s failed to write its settings",
				this
			);
		}
	}
	
	/**
	 * Parses the settings from the provided {@link DataInput} and configures this object appropriately. This method will not necessarily exhaust the input.
	 * @param input a stream to read the settings from
	 * @throws IOException if an I/O error occurs
	 * @throws DecodingException if the settings could not be parsed from input
	 */
	protected abstract void doReadSettings(DataInput input) throws IOException, DecodingException;
	
	/**
	 * Encodes the settings of this model into the provided {@link DataOutput}.
	 * @param output a stream to write the settings into
	 * @throws IOException if an I/O error occurs
	 */
	protected abstract void doWriteSettings(DataOutput output) throws IOException;

}
