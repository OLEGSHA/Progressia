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
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

	R apply(T t) throws E;

	@SuppressWarnings("unchecked")
	default Function<T, R> withHandler(BiConsumer<? super T, ? super E> handler,
			Function<? super T, ? extends R> value) {
		return t -> {
			try {
				return apply(t);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				if (handler != null)
					handler.accept(t, (E) e);
				return value == null ? null : value.apply(t);
			}
		};
	}

	default Function<T, R> withHandler(BiConsumer<? super T, ? super E> handler, Supplier<? extends R> value) {
		return withHandler(handler, t -> value.get());
	}

	default Function<T, R> withHandler(BiConsumer<? super T, ? super E> handler, R value) {
		return withHandler(handler, t -> value);
	}

	default Function<T, R> withHandler(BiConsumer<? super T, ? super E> handler) {
		return withHandler(handler, (Function<T, R>) null);
	}

	public static <T, R, I, E extends Exception> ThrowingFunction<T, R, E> compose(
			ThrowingFunction<? super T, I, ? extends E> first,
			ThrowingFunction<? super I, ? extends R, ? extends E> second) {
		return t -> second.apply(first.apply(t));
	}

	public static <T, R, I, E extends Exception> ThrowingFunction<T, R, E> compose(Function<? super T, I> first,
			ThrowingFunction<? super I, ? extends R, E> second) {
		return t -> second.apply(first.apply(t));
	}

	public static <T, R, I, E extends Exception> ThrowingFunction<T, R, E> compose(
			ThrowingFunction<? super T, I, E> first, Function<? super I, ? extends R> second) {
		return t -> second.apply(first.apply(t));
	}

}
