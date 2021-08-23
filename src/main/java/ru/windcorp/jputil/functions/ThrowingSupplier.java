/*
 * JPUtil
 * Copyright (C)  2019-2021  OLEGSHA/Javapony and contributors
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

package ru.windcorp.jputil.functions;

import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

	T get() throws E;

	@SuppressWarnings("unchecked")
	default Supplier<T> withHandler(Consumer<? super E> handler, Supplier<? extends T> value) {
		return () -> {
			try {
				return get();
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				if (handler != null)
					handler.accept((E) e);
				return value == null ? null : value.get();
			}
		};
	}

	default Supplier<T> withHandler(Consumer<? super E> handler, T value) {
		return withHandler(handler, () -> value);
	}

	default Supplier<T> withHandler(Consumer<? super E> handler) {
		return withHandler(handler, (Supplier<T>) null);
	}

}
