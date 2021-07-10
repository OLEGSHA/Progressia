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

import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;

import java.util.ArrayList;
import java.util.List;

public abstract class Task
		extends Namespaced
		implements Runnable {

	List<Task> requiredTasks = new ArrayList<>();
	private boolean isDone = false;
	private boolean isActive = false;
	private Module owner;

	public Task(String id) {
		super(id);
	}

	public Task(String id, Module module) {
		this(id);
		module.addTask(this);
	}

	@Override
	public void run() {
		if (!canRun()) {
			ArrayList<Task> undoneTasks = new ArrayList<>();
			for (Task j : requiredTasks) {
				if (!j.isDone()) {
					undoneTasks.add(j);
				}
			}

			throw CrashReports.report(new Throwable(),
					"The following required Tasks are not done:\n%s",
					StringUtil.iterableToString(undoneTasks, "\n"));
		} else if (isDone()) {
			throw CrashReports.report(new Throwable(),
					"The task cannot be performed second time");
		} else {
			isActive = true;
			perform();
			isDone = true;
		}
	}

	//This method will be invoked by Run()
	protected abstract void perform();

	public boolean isDone() {
		return isDone;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean canRun() {
		if (this.isActive) return false;
		for (Task reqT : requiredTasks) {
			if (!reqT.isDone()) return false;
		}
		return true;
	}

	public List<Task> getRequiredTasks() {
		return requiredTasks;
	}

	public void addRequiredTask(Task task) {
		requiredTasks.add(task);
	}

	public Module getOwner() {
		return owner;
	}

	public void setOwner(Module module) {
		if (owner != null) {
			CrashReports.crash(
					new Exception("Owner is not null")
					, "Could not set %s as owner of %s, because %s is already owner of it.",
					module.getId(), this.getId(), this.getOwner().getId());
		} else {
			owner = module;
		}
	}
}
