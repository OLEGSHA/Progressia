package ru.windcorp.progressia.common.modules;

import java.util.ArrayList;

import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;

public abstract class Task 
	extends Namespaced
	implements Runnable
	{
	
	private boolean done = false;
	
	ArrayList<Task> requiredTasks = new ArrayList<Task>();

	public Task(String id) {
		super(id);
	}

	@Override
	public void run() {
		if (!canRun()) {
			
			ArrayList<Task> UndoneTasks = new ArrayList<Task>();
			for (Task j : requiredTasks) {
				if (!j.isDone()) {
					UndoneTasks.add(j);
				}
			}
			
			
			throw CrashReports.report(null,
					"The following required Tasks are not done:\n%s",
					StringUtil.iterableToString(UndoneTasks, "\n"));
		}
		
		perform();
		
		done = true;
	}
	
	// This method will be invoked by Run()
	protected abstract void perform();
	
	public void addRequiredTask(Task task) {
		if (task.getId() == this.getId()) {
			throw CrashReports.report(null, 
					"It is impossible for the Task (%s) to require itself.", 
					this.getId());
		}
		requiredTasks.add(task);
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean canRun() {
		for (Task j : requiredTasks) {
			if (!j.isDone()) return false;
		}
		return true;
	}
}
