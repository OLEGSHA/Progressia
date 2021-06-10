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

package ru.windcorp.progressia.common.util.dynstr;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import ru.windcorp.jputil.chars.CharConsumer;
import ru.windcorp.jputil.functions.FloatSupplier;

public class DynamicStrings {

	@FunctionalInterface
	public interface CharSource {
		void flush(CharConsumer sink);
	}

	@FunctionalInterface
	public interface StringSupplier {
		String get();
	}

	public static class Builder {

		private final List<DynamicString.Part> parts = new ArrayList<>();

		public DynamicString build() {
			return new DynamicString(parts.toArray(new DynamicString.Part[parts.size()]));
		}

		public Supplier<CharSequence> buildSupplier() {
			return build().asSupplier();
		}

		public Builder addConst(Object constant) {
			return add(constant.toString());
		}

		public Builder add(String string) {
			return add(string.toCharArray());
		}

		public Builder add(final char[] chars) {
			parts.add(sink -> sink.add(chars));
			return this;
		}

		public Builder add(char c) {
			parts.add(sink -> sink.add(c));
			return this;
		}

		public Builder addDyn(Object obj) {
			if (obj == null)
				return add("null");
			return addDyn(obj::toString);
		}

		public Builder embed(DynamicString str) {
			if (str == null)
				return add("null");

			for (DynamicString.Part p : str.parts) {
				parts.add(p);
			}

			return this;
		}

		public Builder addDyn(Supplier<?> supplier) {
			Objects.requireNonNull(supplier, "supplier");
			return addDyn(() -> Objects.toString(supplier.get()));
		}

		public Builder addDyn(StringSupplier supplier) {
			Objects.requireNonNull(supplier, "supplier");

			parts.add(sink -> {
				String str = supplier.get();
				int length = str.length();

				for (int i = 0; i < length; ++i) {
					sink.add(str.charAt(i));
				}
			});

			return this;
		}

		public Builder addDyn(IntSupplier supplier, int width, boolean alwaysUseSign) {
			Objects.requireNonNull(supplier, "supplier");

			parts.add(sink -> IntFlusher.flushInt(sink, supplier.getAsInt(), width, alwaysUseSign));
			return this;
		}

		public Builder addDyn(IntSupplier supplier, int width) {
			return addDyn(supplier, width, false);
		}

		public Builder addDyn(IntSupplier supplier, boolean alwaysUseSign) {
			return addDyn(supplier, 0, alwaysUseSign);
		}

		public Builder addDyn(IntSupplier supplier) {
			return addDyn(supplier, 0, false);
		}

		public Builder addDyn(DoubleSupplier supplier, int width, int precision, boolean alwaysUseSign) {
			Objects.requireNonNull(supplier, "supplier");

			parts.add(sink -> DoubleFlusher.flushDouble(sink, supplier.getAsDouble(), width, precision, alwaysUseSign));
			return this;
		}

		public Builder addDyn(DoubleSupplier supplier, int width, int precision) {
			return addDyn(supplier, width, precision, false);
		}

		public Builder addDyn(DoubleSupplier supplier, boolean alwaysUseSign, int precision) {
			return addDyn(supplier, 0, precision, alwaysUseSign);
		}

		public Builder addDyn(DoubleSupplier supplier, int precision) {
			return addDyn(supplier, 0, precision, false);
		}

		public Builder addDyn(FloatSupplier supplier, int width, int precision, boolean alwaysUseSign) {
			Objects.requireNonNull(supplier, "supplier");

			parts.add(sink -> FloatFlusher.flushFloat(sink, supplier.getAsFloat(), width, precision, alwaysUseSign));
			return this;
		}

		public Builder addDyn(FloatSupplier supplier, int width, int precision) {
			return addDyn(supplier, width, precision, false);
		}

		public Builder addDyn(FloatSupplier supplier, boolean alwaysUseSign, int precision) {
			return addDyn(supplier, 0, precision, alwaysUseSign);
		}

		public Builder addDyn(FloatSupplier supplier, int precision) {
			return addDyn(supplier, 0, precision, false);
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	private DynamicStrings() {
	}

}
