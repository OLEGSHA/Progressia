package ru.windcorp.progressia.common.modules;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

import java.util.ArrayList;
import java.util.List;

public abstract class Task 
	extends Namespaced
	implements Runnable
	{
	
	private boolean isDone = false;
	private boolean isActive = false;
	
	List<Task> requiredTasks = new ArrayList<>();

	protected Task(String id) {
		super(id);
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

	public boolean isActive() { return isActive; }
	
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
}
