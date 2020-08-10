/*******************************************************************************
 * Progressia
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
package ru.windcorp.progressia.client.graphics.backend;

import java.util.ArrayList;
import java.util.Collection;

/* 
 * FIXME deal with client invocations of .delete() when properly disposing of
 * objects mid-execution
 */

public class OpenGLObjectTracker {
	
	public static interface OpenGLDeletable {
		void delete();
	}
	
	private static final Collection<OpenGLDeletable> TO_DELETE = new ArrayList<>();
	
	public synchronized static void register(OpenGLDeletable object) {
		TO_DELETE.add(object);
	}
	
	public synchronized static void deleteAllObjects() {
		TO_DELETE.forEach(OpenGLDeletable::delete);
		TO_DELETE.clear();
	}

}
