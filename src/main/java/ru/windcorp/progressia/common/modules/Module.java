package ru.windcorp.progressia.common.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

public class Module extends Namespaced {

	private List<Task> tasks = new ArrayList<>();
	private Map<String, String> meta = new HashMap<>();
	private boolean done = false;
	
	
	public Module(String id) {
		super(id);
	}
	
	public Map<String, String> getMeta() {
		return meta;
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	/**
	 * @return false - not all tasks are done
	 */
	public boolean setDone() {
		for (Task t : tasks) {
			if (!t.isDone()) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isDone() {
		return done;
	}
}
