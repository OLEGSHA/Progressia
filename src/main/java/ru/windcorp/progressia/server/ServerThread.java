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

package ru.windcorp.progressia.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;

import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.server.world.ticking.TickerCoordinator;

public class ServerThread implements Runnable {

	private static final ThreadLocal<Server> SERVER_THREADS_MAP = new ThreadLocal<>();

	public static Server getCurrentServer() {
		return SERVER_THREADS_MAP.get();
	}

	private class ServerThreadTracker implements Runnable {

		private final Runnable payload;

		public ServerThreadTracker(Runnable payload) {
			this.payload = payload;
		}

		@Override
		public void run() {
			SERVER_THREADS_MAP.set(getServer());
			payload.run();
		}

	}

	private final Server server;
	private final ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor(r -> new Thread(new ServerThreadTracker(r), "Server thread"));

	private final TickerCoordinator ticker;

	public ServerThread(Server server) {
		this.server = server;
		this.ticker = new TickerCoordinator(server, 1);
	}

	public void start() {
		ticker.start();
		executor.scheduleAtFixedRate(this, 0, 1000 / 20, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		try {
			server.tick();
			ticker.runOneTick();
		} catch (Throwable e) {
			CrashReports.crash(e, "Got a throwable in the server thread");
		}
	}

	public void stop() {
		try {
			executor.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LogManager.getLogger().warn("Received interrupt in ServerThread.stop(), aborting wait");
		}

		getTicker().stop();
	}

	public Server getServer() {
		return server;
	}

	public TickerCoordinator getTicker() {
		return ticker;
	}

}
