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
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListener;
import ru.windcorp.progressia.common.world.ChunkDataListeners;
import ru.windcorp.progressia.server.Server;

/**
 * Central control point for serverside ticking. This class provides an
 * interface to interact with Tickers.
 * 
 * @author javapony
 */
public class TickerCoordinator {

	static final int INITIAL_QUEUE_SIZE = 1024;

	private final Server server;

	// Synchronized manually
	private final Collection<Change> pendingChanges = new HashSet<>(INITIAL_QUEUE_SIZE);
	// Synchronized manually
	private final Collection<Evaluation> pendingEvaluations = new ArrayList<>(INITIAL_QUEUE_SIZE);

	/**
	 * A cached ArrayList used to transfer tasks from Coordinator to Tickers.
	 * This list must be empty when not in
	 * {@link #startPassStage(Collection, String)}.
	 */
	private final Collection<TickerTask> cachedTransferList = new ArrayList<>(INITIAL_QUEUE_SIZE);

	/**
	 * All tasks that must be {@linkplain TickerTask#dispose() disposed of} at
	 * the end of the current tick. This list must be empty when not in
	 * {@link #runPassStage(Collection, String)}.
	 */
	private final Collection<TickerTask> toDispose = new ArrayList<>(INITIAL_QUEUE_SIZE);

	private final Collection<Ticker> tickers;
	private final Collection<Thread> threads;

	private final AtomicInteger workingTickers = new AtomicInteger();

	private final AtomicBoolean canChange = new AtomicBoolean(true);

	private boolean isTickStartSet = false;
	private long tickStart = -1;
	private double tickLength = 1.0 / 20; // Do something about it
	private long ticks = 0;

	private final Logger logger = LogManager.getLogger("Ticker Coordinator");

	public TickerCoordinator(Server server, int tickers) {
		this.server = Objects.requireNonNull(server, "server");

		Collection<Ticker> tickerCollection = new ArrayList<>();

		for (int i = 0; i < tickers; ++i) {
			tickerCollection.add(new Ticker("Ticker " + i, i, this));
		}

		this.tickers = ImmutableList.copyOf(tickerCollection);
		this.threads = Collections2.transform(this.tickers, Ticker::getThread); // Immutable
																				// because
																				// it
																				// is
																				// a
																				// view

		server.getWorld().getData().addListener(ChunkDataListeners.createAdder(new ChunkDataListener() {
			@Override
			public void onChunkChanged(ChunkData chunk) {
				if (!canChange.get()) {
					throw CrashReports.report(null, "A change has been detected during evaluation phase");
				}
			}
		}));
	}

	/*
	 * Public API
	 */

	public synchronized void start() {
		logger.debug("Starting tickers");
		tickers.forEach(Ticker::start);
		logger.debug("Tickers started");
	}

	public synchronized void stop() {
		logger.debug("Stopping tickers");
		tickers.forEach(Ticker::stop);
		logger.debug("Tickers requested to stop");
	}

	public synchronized void requestChange(Change change) {
		pendingChanges.add(change);
	}

	public synchronized void requestEvaluation(Evaluation evaluation) {
		pendingEvaluations.add(evaluation);
	}

	public Server getServer() {
		return server;
	}

	public Collection<Thread> getThreads() {
		return this.threads;
	}

	public double getTickLength() {
		return tickLength;
	}

	public double getTPS() {
		return 1 / tickLength;
	}

	public long getUptimeTicks() {
		return ticks;
	}

	private void onTickStart() {
		long now = System.currentTimeMillis();

		if (isTickStartSet) {
			tickLength = (now - tickStart) * Units.MILLISECONDS;
		} else {
			isTickStartSet = true;
		}

		tickStart = System.currentTimeMillis();
	}

	private void onTickEnd() {
		ticks++;
	}

	/*
	 * runOneTick & Friends
	 */

	public void runOneTick() {
		try {
			onTickStart();

			int passes = 0;

			logger.debug("Beginning tick");

			while (hasPending()) {
				logger.debug("Starting pass");
				runOnePass();
				logger.debug("Pass complete");
				passes++;
			}

			onTickEnd();

			logger.debug("Tick complete; run {} passes", passes);

		} catch (InterruptedException e) {
			// Exit silently

			// ...or almost silently
			logger.debug("Tick interrupted. WTF?");
		} catch (Exception e) {
			throw CrashReports.report(e, "Coordinator");
		}
	}

	private boolean hasPending() {
		// Race condition?
		return !(pendingChanges.isEmpty() && pendingEvaluations.isEmpty());
	}

	private synchronized void runOnePass() throws InterruptedException {
		canChange.set(false);
		runPassStage(pendingEvaluations, "EVALUATION");
		canChange.set(true);
		runPassStage(pendingChanges, "CHANGE");
	}

	private synchronized void runPassStage(Collection<? extends TickerTask> tasks, String stageName)
			throws InterruptedException {
		if (!toDispose.isEmpty())
			throw new IllegalStateException("toDispose is not empty: " + toDispose);

		Collection<TickerTask> toDispose = this.toDispose;

		startPassStage(tasks, toDispose, stageName);
		sync();
		dispose(toDispose);
	}

	private void dispose(Collection<TickerTask> toDispose) {
		toDispose.forEach(TickerTask::dispose);
		toDispose.clear();
	}

	private synchronized void startPassStage(Collection<? extends TickerTask> tasks, Collection<TickerTask> toDispose,
			String stageName) {
		if (tasks.isEmpty()) {
			logger.debug("Skipping stage {}: tasks is empty", stageName);
			return;
		}

		logger.debug("Starting stage {}", stageName);

		if (!cachedTransferList.isEmpty())
			throw new IllegalStateException("cachedTransferList is not empty: " + cachedTransferList);

		workingTickers.set(0);

		for (Ticker ticker : tickers) {
			workingTickers.incrementAndGet();

			Collection<TickerTask> selectedTasks = cachedTransferList;
			ticker.requestWork(selectTasks(ticker, tasks, selectedTasks));
			selectedTasks.clear();
		}

		toDispose.addAll(tasks);
		tasks.clear();

		logger.debug("Stage started");
	}

	private Collection<TickerTask> selectTasks(Ticker ticker, Collection<? extends TickerTask> tasks,
			Collection<TickerTask> output) {
		// TODO implement properly

		for (TickerTask task : tasks) {
			// Assign to one ticker randomly
			if (task.hashCode() % tickers.size() == ticker.getId()) {
				output.add(task);
			}
		}

		return output;
	}

	private synchronized void sync() throws InterruptedException {
		logger.debug("Starting sync wait");
		while (workingTickers.get() > 0) {
			this.wait();
			logger.debug("Sync notification received");
		}
		logger.debug("Sync achieved");
	}

	/*
	 * Interface for Tickers
	 */

	synchronized void reportWorkComplete() {
		int stillWorking = workingTickers.decrementAndGet();
		if (stillWorking < 0)
			throw new IllegalStateException("stillWorking = " + stillWorking);

		if (stillWorking != 0) {
			logger.debug("stillWorking = {}, not notifying sync", stillWorking);
			return;
		}

		logger.debug("All tickers reported completion, notifying sync");

		this.notifyAll();
	}

	void crash(Throwable t, String thread) {
		if (t instanceof ConcurrentModificationException) {
			logger.debug("javahorse kill urself");
		}

		throw CrashReports.crash(t, "Something has gone horribly wrong in server ticker code "
				+ "(thread %s) and it is (probably) not related to mods or devils.", thread);
	}

}
