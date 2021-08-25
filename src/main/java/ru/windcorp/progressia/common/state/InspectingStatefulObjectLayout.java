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
 
package ru.windcorp.progressia.common.state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import ru.windcorp.progressia.common.state.codec.ObjectCodec;

public class InspectingStatefulObjectLayout
	extends AbstractStatefulObjectLayout {

	private final List<StateField> fields = new ArrayList<>();

	private final PrimitiveCounters fieldIndexCounters = new PrimitiveCounters();

	public InspectingStatefulObjectLayout(String objectId) {
		super(objectId);
	}

	@Override
	public StateStorage instantiateStorage() {
		return new HashMapStateStorage();
	}

	@Override
	protected int getFieldCount() {
		return fields.size();
	}

	@Override
	protected StateField getField(int fieldIndex) {
		return fields.get(fieldIndex);
	}

	public StatefulObjectLayout compile() {
		return new OptimizedStatefulObjectLayout(
			getObjectId(),
			fields,
			fieldIndexCounters
		);
	}

	private <T extends StateField> T registerField(T field) {
		fields.add(field);
		return field;
	}

	@Override
	public StateFieldBuilder getBuilder(String id) {
		return new InspectingStateFieldBuilder(id);
	}

	private class InspectingStateFieldBuilder implements StateFieldBuilder {

		private class Int implements StateFieldBuilder.Int {

			@Override
			public IntStateField build() {
				return registerField(
					new IntStateField(
						id,
						isLocal,
						fieldIndexCounters.getIntsThenIncrement()
					)
				);
			}

		}
		
		private class Obj<T> implements StateFieldBuilder.Obj<T> {

			private final ObjectCodec<T> codec;
			private final Supplier<T> defaultValue;

			public Obj(ObjectCodec<T> codec, Supplier<T> defaultValue) {
				this.codec = codec;
				this.defaultValue = defaultValue;
			}

			@Override
			public ObjectStateField<T> build() {
				return registerField(
					new ObjectStateField<T>(
						id,
						isLocal,
						fieldIndexCounters.getObjectsThenIncrement(),
						codec,
						defaultValue
					)
				);
			}

		}

		private final String id;

		private boolean isLocal = true;

		public InspectingStateFieldBuilder(String id) {
			this.id = id;
		}

		@Override
		public Int ofInt() {
			return new Int();
		}
		
		@Override
		public <T> Obj<T> of(ObjectCodec<T> codec, Supplier<T> defaultValue) {
			return new Obj<T>(codec, defaultValue);
		}

		@Override
		public StateFieldBuilder setLocal(boolean isLocal) {
			this.isLocal = isLocal;
			return this;
		}

		@Override
		public void setOrdinal(int ordinal) {
			if (ordinal != fields.size()) {
				throw new IllegalStateException(
					"This field is going to receive ordinal "
						+ fields.size() + ", requested ordinal "
						+ ordinal
				);
			}
		}

	}

}
