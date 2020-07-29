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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class Namespaced extends Named {
	
	private static final char SEPARATOR = ':';
	
	private static final String PART_REGEX = "^[A-Z][a-zA-Z0-9]{2,}$";
	
	private static final Predicate<String> PART_CHECKER =
			Pattern.compile(PART_REGEX).asPredicate();
	
	private final String namespace;
	private final String id;

	public Namespaced(String namespace, String name) {
		super(name);
		this.namespace = Objects.requireNonNull(namespace, "namespace");
		this.id = namespace + SEPARATOR + name;
		
		if (!PART_CHECKER.test(name)) {
			throw new IllegalArgumentException(
					"Name \"" + name + "\" is invalid. "
							+ "Allowed is: " + PART_REGEX
			);
		}
		
		if (!PART_CHECKER.test(namespace)) {
			throw new IllegalArgumentException(
					"Namespace \"" + namespace + "\" is invalid. "
							+ "Allowed is: " + PART_REGEX
			);
		}
	}
	
	public String getId() {
		return id;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Namespaced other = (Namespaced) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
