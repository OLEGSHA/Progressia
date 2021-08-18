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
import ru.windcorp.jputil.chars.StringUtil;

public abstract class NamedParameterizedSelector<T> extends NamedSelector<T> {

	private final char separator;
	private String givenName;

	public NamedParameterizedSelector(char separator, String... names) {
		super(names);
		this.separator = separator;
	}

	@Override
	public Selector<T> derive(String name) throws SyntaxException {
		String[] parts = StringUtil.split(name, separator, 2);

		if (parts[1] == null) {
			return null;
		}

		if (!matchesName(parts[0])) {
			return null;
		}

		NamedParameterizedSelector<T> selector = deriveImpl(parts[1]);
		selector.givenName = name;
		return selector;
	}

	protected abstract NamedParameterizedSelector<T> deriveImpl(String param) throws SyntaxException;

	@Override
	public String toString() {
		return givenName;
	}

}
