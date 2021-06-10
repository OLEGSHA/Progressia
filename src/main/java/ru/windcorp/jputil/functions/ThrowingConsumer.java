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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

	void accept(T t) throws E;

	@SuppressWarnings("unchecked")
	default Consumer<T> withHandler(BiConsumer<? super T, ? super E> handler) {
		return t -> {
			try {
				accept(t);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				handler.accept(t, (E) e);
			}
		};
	}

	public static <T, E extends Exception> ThrowingConsumer<T, E> concat(ThrowingConsumer<? super T, ? extends E> first,
			ThrowingConsumer<? super T, ? extends E> second) {
		return t -> {
			first.accept(t);
			second.accept(t);
		};
	}

	public static <T, E extends Exception> ThrowingConsumer<T, E> concat(Consumer<? super T> first,
			ThrowingConsumer<? super T, ? extends E> second) {
		return t -> {
			first.accept(t);
			second.accept(t);
		};
	}

	public static <T, E extends Exception> ThrowingConsumer<T, E> concat(ThrowingConsumer<? super T, ? extends E> first,
			Consumer<? super T> second) {
		return t -> {
			first.accept(t);
			second.accept(t);
		};
	}

}
