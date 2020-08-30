package ru.windcorp.progressia.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ru.windcorp.progressia.server.world.Ticker;

public class ServerThread implements Runnable {
	
	private static final ThreadLocal<Server> SERVER_THREADS_MAP =
			new ThreadLocal<>();
	
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
	private final ScheduledExecutorService executor =
			Executors.newSingleThreadScheduledExecutor(
					r -> new Thread(new ServerThreadTracker(r), "Server thread")
			);
	
	private final Ticker ticker;
	
	public ServerThread(Server server) {
		this.server = server;
		this.ticker = new Ticker(server);
	}
	
	public void start() {
		executor.scheduleAtFixedRate(this, 0, 1000 / 20, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void run() {
		server.tick();
		ticker.run();
	}
	
	public Server getServer() {
		return server;
	}

}
