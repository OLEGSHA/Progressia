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

import org.apache.logging.log4j.LogManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class TaskManager {
	private static final TaskManager instance = new TaskManager();

	private final Set<Task> tasks = new HashSet<>();
	private final Set<Module> modules = new HashSet<>();
	private final ExecutorService executorService;

	private final AtomicBoolean loadingDone;
	private final AtomicInteger activeThreadsCount;

	private final Map<Thread, Task> loadersMonitorMap;
	Map<Thread, Task> unmodifiableLoadersMonitorMap;

	private TaskManager() {
		loadingDone = new AtomicBoolean(false);
		activeThreadsCount = new AtomicInteger(0);
		executorService = newFixedThreadPool(
				Runtime.getRuntime().availableProcessors(), Thread::new);
		loadersMonitorMap = new HashMap<>(Runtime.getRuntime().availableProcessors());
		unmodifiableLoadersMonitorMap = Collections.unmodifiableMap(loadersMonitorMap);
	}

	public static TaskManager getInstance() {
		return instance;
	}

	/**
	 * Registers the module and its tasks that are
	 * to be performed by {@link TaskManager#startLoading()}.
	 *
	 * @param module from where to register tasks for loading.
	 */
	public void registerModule(Module module) {
		tasks.addAll(module.getTasks());
		modules.add(module);
	}

	/**
	 * Registers a task that is to be performed
	 * by {@link TaskManager#startLoading()}.
	 *
	 * @param task to register for loading.
	 */
	public void addTask(Task task) {
		tasks.add(task);
	}

	public boolean isLoadingDone() {
		return loadingDone.get();
	}

	/**
	 * The method to start loading. It will perform every registered task.
	 */
	public void startLoading() {
		LogManager.getLogger().info("Loading is started");
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			executorService.submit(() -> {
				while (!loadingDone.get()) {
					Task t = getRunnableTask();
					if (t != null) {
						activeThreadsCount.incrementAndGet();
						loadersMonitorMap.put(Thread.currentThread(), t);
						t.run();
						loadersMonitorMap.put(Thread.currentThread(), null);
						activeThreadsCount.decrementAndGet();
						synchronized (this) {
							notifyAll();
						}
					} else if (activeThreadsCount.get() > 0) {
						try {
							synchronized (this) {
								this.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						loadingDone.set(true);
						synchronized (this) {
							notifyAll();
						}
					}
				}
			});
		}

		waitForLoadingEnd();
		if (!tasks.isEmpty()) {
			throw CrashReports.crash(new Exception("Loading is failed"), "");
		}
		LogManager.getLogger().info("Loading is finished");
		executorService.shutdownNow();
	}

	/**
	 * @return Task - founded registered task with {@link Task#canRun()} = true;
	 * null - there is no available task found.
 	 * @see Task#canRun()
	 */
	private synchronized Task getRunnableTask() {
		if (!tasks.isEmpty()) {
			for (Task t :
					tasks) {
				if (t.canRun()) {
					tasks.remove(t);
					return t;
				}
			}
		}
		return null;
	}

	/**
	 * Makes the thread that is performing this method
	 * to wait until the loading is not done.
	 */
	private void waitForLoadingEnd() {
		synchronized (this) {
			while (!loadingDone.get()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return a map where key is a thread making loading
	 * and where value is a task that is being performed by it
	 * at the moment.
	 */
	public Map<Thread, Task> getLoadersMonitorMap() {
		return unmodifiableLoadersMonitorMap;
	}

}
