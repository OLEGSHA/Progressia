package ru.windcorp.progressia.common.modules;


import org.apache.logging.log4j.LogManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class TaskManager {
	private static final TaskManager instance = new TaskManager();
	private final List<Task> tasks = new ArrayList<>();
	private final List<Module> modules = new ArrayList<>();
	private final ExecutorService executorService;
	private final AtomicBoolean loadingDone;
	private final AtomicInteger activeThreadsCount;

	private TaskManager() {
		loadingDone = new AtomicBoolean(false);
		activeThreadsCount = new AtomicInteger(0);
		executorService = newFixedThreadPool(
				Runtime.getRuntime().availableProcessors(), Thread::new);
	}

	public static TaskManager getInstance() {
		return instance;
	}

	public void registerModule(Module module) {
		tasks.addAll(module.getTasks());
		modules.add(module);
	}

	public boolean isLoadingDone() {
		return loadingDone.get();
	}

	public void startLoading() {
		LogManager.getLogger().info("Loading is started");
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			executorService.submit(() -> {
				while (!loadingDone.get()) {
					Task t = getRunnableTask();
					if (t != null) {
						activeThreadsCount.incrementAndGet();
						t.run();
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

	public synchronized Task getRunnableTask() {
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

}
