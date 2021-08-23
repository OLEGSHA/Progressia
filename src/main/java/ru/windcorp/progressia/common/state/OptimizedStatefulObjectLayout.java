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

import java.util.List;

import com.google.common.collect.ImmutableList;

public class OptimizedStatefulObjectLayout extends AbstractStatefulObjectLayout {

	private final List<StateField> fields;
	private final PrimitiveCounters sizes;

	public OptimizedStatefulObjectLayout(String objectId, List<StateField> fields, PrimitiveCounters counters) {
		super(objectId);
		this.fields = ImmutableList.copyOf(fields);
		this.sizes = new PrimitiveCounters(counters);
	}

	@Override
	protected int getFieldCount() {
		return fields.size();
	}

	@Override
	protected StateField getField(int fieldIndex) {
		return fields.get(fieldIndex);
	}

	@Override
	public StateStorage createStorage() {
		return new OptimizedStateStorage(sizes);
	}

	@Override
	public StateFieldBuilder getBuilder(String id) {
		return new RetrieverStateFieldBuilder();
	}

	private class RetrieverStateFieldBuilder implements StateFieldBuilder {

		private StateField result;

		@Override
		public Int ofInt() {
			return new Int() {
				@Override
				public IntStateField build() {
					return (IntStateField) result;
				}
			};
		}

		@Override
		public StateFieldBuilder setLocal(boolean isLocal) {
			return this;
		}

		@Override
		public void setOrdinal(int ordinal) {
			this.result = fields.get(ordinal);
		}

	}

}
