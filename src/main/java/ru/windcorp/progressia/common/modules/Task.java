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
	
	ArrayList<Task> requiredTasks = new ArrayList<>();

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
	
	public void addRequiredTask(Task task) {
		if (task.equals(this)) {
			throw CrashReports.report(new Throwable(), 
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
