package ru.windcorp.progressia.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;

import ru.windcorp.progressia.server.world.ticking.TickerCoordinator;

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
		} catch (Exception e) {
			LogManager.getLogger(getClass()).error("Got an exception in server thread", e);
		}
	}

	public Server getServer() {
		return server;
	}
	
	public TickerCoordinator getTicker() {
		return ticker;
	}

}
