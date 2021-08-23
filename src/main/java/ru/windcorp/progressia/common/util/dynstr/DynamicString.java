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

import java.util.function.Supplier;

import gnu.trove.list.TCharList;
import gnu.trove.list.array.TCharArrayList;
import ru.windcorp.jputil.chars.CharConsumer;

public final class DynamicString implements CharSequence {

	interface Part {
		void flush(TCharList sink);
	}

	@FunctionalInterface
	public interface CharFlusherPart {
		void flush(CharConsumer sink);
	}

	final TCharList chars = new TCharArrayList();
	final Part[] parts;

	private int hashCode = 0;

	DynamicString(Part[] parts) {
		this.parts = parts;
	}

	/**
	 * Causes the contents of this string to be reevaluated. This is not
	 * currently thread-safe, take caution.
	 */
	public void update() {
		chars.clear();
		hashCode = 0;

		for (Part part : parts) {
			part.flush(chars);
		}
	}

	public Supplier<CharSequence> asSupplier() {
		return () -> {
			update();
			return this;
		};
	}

	@Override
	public int length() {
		return chars.size();
	}

	@Override
	public char charAt(int index) {
		return chars.get(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new SubString(start, end);
	}

	@Override
	public String toString() {
		int length = length();

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; ++i) {
			sb.append(chars.get(i));
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		int h = hashCode;
		int length = length();

		if (h != 0 || length == 0)
			return h;

		for (int i = 0; i < length; i++) {
			h = 31 * h + this.chars.get(i);
		}

		hashCode = h;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != getClass())
			return false;

		DynamicString other = (DynamicString) obj;

		if (hashCode() != this.hashCode())
			return false;

		return other.chars.equals(this.chars);
	}

	private class SubString implements CharSequence {

		private final int start;
		private final int end;

		public SubString(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public int length() {
			return Math.min(end, DynamicString.this.length()) - start;
		}

		@Override
		public char charAt(int index) {
			if (index < 0 || index > length()) {
				throw new IndexOutOfBoundsException(Integer.toString(index) + " is out of bounds");
			}

			return DynamicString.this.charAt(index);
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			if (start < 0)
				throw new IllegalArgumentException("start (" + start + ") is negative");
			if (end < start)
				throw new IllegalArgumentException("end (" + end + ") < start (" + start + ")");

			int absoluteStart = this.start + start;
			int absoluteEnd = this.start + end;

			return DynamicString.this.subSequence(absoluteStart, absoluteEnd);
		}

	}

}
