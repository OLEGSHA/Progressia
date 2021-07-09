package ru.windcorp.progressia.common.modules;

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
		isActive = true;
		perform();
		isDone = true;
	}

	// This method will be invoked by Run()
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

	public void setOwner(Module module) {
		if(owner != null) {
			CrashReports.crash(
					new Exception("Owner is not null")
					, "Could not set %s as owner of %s, because %s is already owner of it.",
					module.getId(), this.getId(), this.getOwner().getId());
		} else {
			owner = module;
		}
	}

	public Module getOwner() {
		return owner;
	}
}
