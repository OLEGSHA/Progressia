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
package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.state.codec.ObjectCodec;

public class SpeciesCodec extends ObjectCodec<SpeciesDatalet> {

	public SpeciesCodec() {
		super(SpeciesDatalet.class);
	}

	@Override
	protected SpeciesDatalet doRead(SpeciesDatalet previous, DataInput input, IOContext context) throws IOException {
		String id = input.readUTF();
		
		SpeciesDatalet result = previous;
		
		if (result == null || !result.getSpecies().getId().equals(id)) {
			SpeciesData species = SpeciesDataRegistry.getInstance().get(id);
			if (species == null) {
				throw new IOException("Unknown species ID " + species);
			}
			result = species.createDatalet();
		}
		
		result.read(input, context);
		
		return result;
	}

	@Override
	protected void doWrite(SpeciesDatalet obj, DataOutput output, IOContext context) throws IOException {
		output.writeUTF(obj.getSpecies().getId());
		obj.write(output, context);
	}

	@Override
	public SpeciesDatalet copy(SpeciesDatalet object, SpeciesDatalet previous) {
		SpeciesDatalet result = previous;
		
		if (result == null || result.getSpecies() != object.getSpecies()) {
			result = object.getSpecies().createDatalet();
		}
		
		object.copy(result);
		
		return result;
	}

}
