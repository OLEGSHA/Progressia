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

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Exception> {

	@FunctionalInterface
	public static interface BiConsumerHandler<T, U, E extends Exception> {
		void handle(T t, U u, E e);
	}

	void accept(T t, U u) throws E;

	@SuppressWarnings("unchecked")
	default BiConsumer<T, U> withHandler(BiConsumerHandler<? super T, ? super U, ? super E> handler) {
		return (t, u) -> {
			try {
				accept(t, u);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				handler.handle(t, u, (E) e);
			}
		};
	}

	public static <T, U, E extends Exception> ThrowingBiConsumer<T, U, E> concat(
			ThrowingBiConsumer<? super T, ? super U, ? extends E> first,
			ThrowingBiConsumer<? super T, ? super U, ? extends E> second) {
		return (t, u) -> {
			first.accept(t, u);
			second.accept(t, u);
		};
	}

	public static <T, U, E extends Exception> ThrowingBiConsumer<T, U, E> concat(BiConsumer<? super T, ? super U> first,
			ThrowingBiConsumer<? super T, ? super U, E> second) {
		return (t, u) -> {
			first.accept(t, u);
			second.accept(t, u);
		};
	}

	public static <T, U, E extends Exception> ThrowingBiConsumer<T, U, E> concat(
			ThrowingBiConsumer<? super T, ? super U, E> first, BiConsumer<? super T, ? super U> second) {
		return (t, u) -> {
			first.accept(t, u);
			second.accept(t, u);
		};
	}

}
