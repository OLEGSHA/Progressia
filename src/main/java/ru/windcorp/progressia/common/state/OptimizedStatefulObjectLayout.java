package ru.windcorp.progressia.common.state;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class OptimizedStatefulObjectLayout
extends AbstractStatefulObjectLayout {

	private final List<StateField> fields;
	private final PrimitiveCounters sizes;

	public OptimizedStatefulObjectLayout(
			String objectId,
			List<StateField> fields, PrimitiveCounters counters
	) {
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
