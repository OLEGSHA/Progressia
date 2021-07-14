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
package ru.windcorp.progressia.common.modules;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

import java.util.*;

public class Module extends Namespaced {

	private final Set<Task> tasks = new HashSet<>();
	private final Map<String, String> meta = new HashMap<>();

	/**
	 * @param id the identifier of a task object.
	 * Its format is restricted by {@link Namespaced}.
	 * @see Namespaced#Namespaced
	 */
	public Module(String id) {
		super(id);
		meta.put("id", id);
	}

	/**
	 * @return meta information of the module as {@link Map}.
	 */
	public Map<String, String> getMeta() {
		return Collections.unmodifiableMap(meta);
	}

	public Set<Task> getTasks() {
		return Collections.unmodifiableSet(tasks);
	}

	/**
	 * @param task that will be attached to the module.
	 * A task can't be added to any module second time.
	 */
	public void addTask(Task task) {
		task.setOwner(this);
		tasks.add(task);
	}

}
