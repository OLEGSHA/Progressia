package ru.windcorp.progressia.common.modules;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

import java.util.*;

public class Module extends Namespaced {

	private final List<Task> tasks = new ArrayList<>();
	private final Map<String, String> meta = new HashMap<>();
	private final Map<String, String> unmodifiableMeta = Collections.unmodifiableMap(meta);


	public Module(String id) {
		super(id);
	}

	public Map<String, String> getMeta() {
		return unmodifiableMeta;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void addTask(Task task) {
		task.setOwner(this);
		tasks.add(task);
	}

}
