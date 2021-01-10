package ru.windcorp.progressia.common.modules;

import java.util.ArrayList;
import java.util.List;

import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;

public abstract class Task 
	extends Namespaced
	implements Runnable
	{
	
	private boolean done = false;
	
	List<Task> requiredTasks = new ArrayList<>();

	protected Task(String id) {
		super(id);
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
		}
		
		perform();
		
		done = true;
	}
	
	// This method will be invoked by Run()
	protected abstract void perform();
	
	public boolean isDone() {
		return done;
	}
	
	public boolean canRun() {
		for (Task t : requiredTasks) {
			if (!t.isDone()) return false;
		}
		return true;
	}
	
	public List<Task> getRequiredTasks() {
		return requiredTasks;
	}
}
