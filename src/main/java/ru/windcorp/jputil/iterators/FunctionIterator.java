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

package ru.windcorp.jputil.iterators;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Javapony
 */
public class FunctionIterator<T, E> implements Iterator<E> {

	private final Iterator<T> parent;
	private final Function<T, E> function;

	public FunctionIterator(Iterator<T> parent, Function<T, E> function) {
		this.parent = parent;
		this.function = function;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return parent.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		return function.apply(parent.next());
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		parent.remove();
	}

}
