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

package ru.windcorp.progressia.server.world.ticking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.server.Server;

class Ticker {

	private final String name;
	private final int id;

	private Thread thread = null;
	private final TickerCoordinator coordinator;

	private volatile boolean shouldRun = true;

	// Expected to implement RandomAccess
	private final List<TickerTask> tasks = new ArrayList<>(TickerCoordinator.INITIAL_QUEUE_SIZE);

	private final Logger logger;

	public Ticker(String name, int id, TickerCoordinator coordinator) {
		this.name = Objects.requireNonNull(name, "name");
		this.id = id;
		this.coordinator = Objects.requireNonNull(coordinator, "coordinator");

		this.logger = LogManager.getLogger(this.name);
	}

	public synchronized void start() {
		if (thread != null)
			throw new IllegalStateException("Ticker already started in thread " + thread);

		thread = new Thread(this::run, this.name);
		logger.debug("Starting");
		thread.start();
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Thread getThread() {
		return thread;
	}

	public TickerCoordinator getCoordinator() {
		return coordinator;
	}

	public synchronized void stop() {
		if (thread == null)
			return;

		shouldRun = false;
		thread.interrupt();

		logger.debug("Stopping");
	}

	public synchronized void requestWork(Collection<TickerTask> tasks) {
		int currentTaskCount = this.tasks.size();
		if (currentTaskCount != 0) {
			throw new IllegalStateException("Ticker already has " + currentTaskCount + " tasks");
		}

		this.tasks.addAll(Objects.requireNonNull(tasks, "tasks"));
		this.notifyAll();

		logger.debug("Work {} requested", tasks.size());
	}

	private void run() {
		try {
			logger.debug("Started");

			while (!Thread.interrupted()) {
				boolean shouldStop = sleep();
				if (shouldStop)
					break;
				work();
			}

			logger.debug("Stopped");

			// Do not release Thread reference so start() still throws ISE
		} catch (Exception e) {
			getCoordinator().crash(e, this.name);
		}
	}

	private synchronized boolean sleep() {
		logger.debug("Entering sleep");

		try {
			while (true) {

				if (!shouldRun) {
					logger.debug("Exiting sleep: received stop request");
					return true;
				}

				int taskCount = tasks.size();
				if (taskCount > 0) {
					logger.debug("Exiting sleep: received {} tasks", taskCount);
					return false;
				}

				logger.debug("Waiting");
				this.wait();

			}
		} catch (InterruptedException e) {
			logger.debug("Exiting sleep: interrupted");
			return true;
		}
	}

	private void work() {
		logger.debug("Starting work");

		int tasksCompleted = runTasks();
		resetState();

		logger.debug("Work complete; run {} tasks", tasksCompleted);
	}

	private int runTasks() {
		int tasksCompleted = 0;

		Server srv = getCoordinator().getServer();

		for (int i = 0; i < tasks.size(); ++i) {
			TickerTask task = tasks.get(i);

			assert task != null : "Encountered null task";

			try {
				task.run(srv);
			} catch (Exception e) {
				throw CrashReports.report(e, "Could not run %s task %s", task.getClass().getSimpleName(), task);
			}

			tasksCompleted++;
		}

		return tasksCompleted;
	}

	private void resetState() {
		tasks.clear();
		getCoordinator().reportWorkComplete();
	}

}
