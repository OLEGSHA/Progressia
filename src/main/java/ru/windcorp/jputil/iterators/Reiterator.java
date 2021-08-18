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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Reiterator<E> implements Iterable<E> {

	private class ReiteratorIterator implements Iterator<E> {

		int index = 0;

		@Override
		public boolean hasNext() {
			synchronized (source) {
				if (index >= data.size()) {
					if (!source.hasNext()) {
						return false;
					} else {
						data.add(source.next());
					}
				}

				return true;
			}
		}

		@Override
		public E next() {
			E result;
			synchronized (source) {
				if (!hasNext())
					throw new NoSuchElementException();
				result = data.get(index);
			}
			index++;
			return result;
		}

	}

	private final Iterator<E> source;
	private final ArrayList<E> data = new ArrayList<>();

	public Reiterator(Iterator<E> source) {
		this.source = source;
	}

	@Override
	public Iterator<E> iterator() {
		return new ReiteratorIterator();
	}

}
