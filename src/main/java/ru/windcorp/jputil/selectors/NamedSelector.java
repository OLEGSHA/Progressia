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

package ru.windcorp.jputil.selectors;

import ru.windcorp.jputil.SyntaxException;

public abstract class NamedSelector<T> implements Selector<T> {

	private final String[] names;

	public NamedSelector(String... names) {
		this.names = names;
	}

	public boolean matchesName(String name) {
		for (String n : names) {
			if (n.equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Selector<T> derive(String name) throws SyntaxException {
		return matchesName(name) ? this : null;
	}

	@Override
	public String toString() {
		return names[0];
	}

}
