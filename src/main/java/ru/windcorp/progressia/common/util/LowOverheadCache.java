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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LowOverheadCache<E> {

	private final ThreadLocal<List<E>> ready = ThreadLocal.withInitial(ArrayList::new);

	private final Supplier<E> generator;

	public LowOverheadCache(Supplier<E> generator) {
		this.generator = generator;
	}

	public E grab() {
		List<E> list = ready.get();
		int size = list.size();

		if (size == 0) {
			return generator.get();
		}

		return list.remove(size - 1);
	}

	public void release(E object) {
		ready.get().add(object);
	}

}
