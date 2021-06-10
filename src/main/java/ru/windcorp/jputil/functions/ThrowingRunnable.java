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

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {

	void run() throws E;

	@SuppressWarnings("unchecked")
	default Runnable withHandler(Consumer<? super E> handler) {
		return () -> {
			try {
				run();
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				handler.accept((E) e);
			}
		};
	}

	public static <E extends Exception> ThrowingRunnable<E> concat(ThrowingRunnable<? extends E> first,
			ThrowingRunnable<? extends E> second) {
		return () -> {
			first.run();
			second.run();
		};
	}

	public static <E extends Exception> ThrowingRunnable<E> concat(Runnable first, ThrowingRunnable<E> second) {
		return () -> {
			first.run();
			second.run();
		};
	}

	public static <E extends Exception> ThrowingRunnable<E> concat(ThrowingRunnable<E> first, Runnable second) {
		return () -> {
			first.run();
			second.run();
		};
	}

}
