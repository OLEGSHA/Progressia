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
import java.util.NoSuchElementException;

public class PeekingIterator<E> implements Iterator<E> {

	private final Iterator<? extends E> source;
	private E next = null;

	public PeekingIterator(Iterator<? extends E> source) {
		this.source = source;
	}

	@Override
	public boolean hasNext() {
		return next != null || source.hasNext();
	}

	public E peek() {
		if (next == null) {
			if (source.hasNext()) {
				next = source.next();
			} else {
				throw new NoSuchElementException();
			}
		}

		return next;
	}

	// SonarLint: "Iterator.next()" methods should throw
	// "NoSuchElementException" (java:S2272)
	// peek() throws NoSuchElementException as expected
	@SuppressWarnings("squid:S2272")

	@Override
	public E next() {
		E element = peek();
		next = null;
		return element;
	}

}
