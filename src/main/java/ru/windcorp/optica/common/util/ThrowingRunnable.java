/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.optica.common.util;

import java.util.function.Consumer;

import com.google.common.base.Throwables;

@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {
	
	void run() throws T;
	
	default Runnable withCatcher(
			Consumer<T> catcher,
			Class<T> throwableClass
	) {
		return () -> {
			
			try {
				ThrowingRunnable.this.run();
			} catch (Throwable t) {
				if (t.getClass() == throwableClass) {
					catcher.accept(throwableClass.cast(t));
				}
				
				Throwables.throwIfUnchecked(t);
				
				// This should never happen
				throw new AssertionError("This should not have been thrown", t);
			}
			
		};
	}
	
	default Runnable withCatcher(
			Consumer<Throwable> catcher
	) {
		return () -> {
			try {
				ThrowingRunnable.this.run();
			} catch (Throwable t) {
				catcher.accept(t);
			}
		};
	}

}
