/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
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

package ru.windcorp.progressia.common.util.crash;

import java.util.Map;

/**
 * A crash report utility that gathers information about game and system state
 * when a crash occurs and presents it to the user via the crash report.
 * ContextProviders are not aware of the nature of the problem, unlike
 * {@link Analyzer}s.
 * 
 * @see Analyzer
 * @author serega404
 */
public interface ContextProvider {

	/**
	 * Provides human-readable description of the state of the game and the
	 * system. This information is {@link Map#put(Object, Object) put} into the
	 * provided map as key-value pairs. Keys are the characteristic being
	 * described, such as "OS Name", and should be Strings In Title Case With
	 * Spaces. If this provider cannot provide any information at this moment,
	 * the map is not modified.
	 * 
	 * @param output
	 *            the map to append output to
	 */
	void provideContext(Map<String, String> output);

	/**
	 * Returns this provider's human-readable name. It should be A String In
	 * Title Case With Spaces.
	 * 
	 * @return this provider's name
	 */
	String getName();
}
