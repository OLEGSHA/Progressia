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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Task
		extends Namespaced
		implements Runnable {

	private final Set<Task> requiredTasks = new HashSet<>();
	private final AtomicBoolean isDone = new AtomicBoolean(false);
	private final AtomicBoolean isActive = new AtomicBoolean(false);
	private Module owner;

	/**
	 * @param id the identifier of a task object.
	 * Its format is restricted by {@link Namespaced}.
	 * @see Namespaced#Namespaced
	 */
	public Task(String id) {
		super(id);
	}

	/**
	 * @param id the identifier of a task object.
	 * Its format is restricted by {@link Namespaced}.
	 * @param module to which the task will be attached.
	 * @see Namespaced#Namespaced
	 */
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
			isActive.set(true);
			perform();
			isActive.set(false);
			isDone.set(true);
		}
	}

	/**
	 * The method is to be invoked in run().
	 * @see Task#run()
	 */
	protected abstract void perform();

	public boolean isDone() {
		return isDone.get();
	}

	/**
	 * @return if the {@link Task#run()} method is being invoked at the moment or not.
	 */
	public boolean isActive() {
		return isActive.get();
	}

	/**
	 *
	 * @return true - the method is not done and not active
	 * and all requirement tasks are done, false - otherwise.
	 */
	public boolean canRun() {
		if (this.isActive.get() || isDone.get()) return false;
		for (Task reqT : requiredTasks) {
			if (!reqT.isDone()) return false;
		}
		return true;
	}

	public Set<Task> getRequiredTasks() {
		return requiredTasks;
	}

	public void addRequiredTask(Task task) {
		requiredTasks.add(task);
	}

	/**
	 * @return the module the task is attached to.
	 */
	public Module getOwner() {
		return owner;
	}

	/**
	 * @param module to which the task will be attached.
	 * Only one module can be the owner of the task.
 	 */
	public void setOwner(Module module) {
		if (owner != null) {
			CrashReports.crash(
					new Throwable()
					, "Could not set %s as owner of %s, because %s is already owner of it.",
					module.getId(), this.getId(), this.getOwner().getId());
		} else {
			owner = module;
		}
	}
}
