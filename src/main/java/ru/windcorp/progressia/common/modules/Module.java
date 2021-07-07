package ru.windcorp.progressia.common.modules;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Module extends Namespaced {

	private final List<Task> tasks = new ArrayList<>();
	private final Map<String, String> meta = new HashMap<>();
	private final boolean done = false;


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
	public boolean done() {
		for (Task t : tasks) {
			if (!t.isDone()) {
				return false;
			}
		}

		return true;
	}
}
