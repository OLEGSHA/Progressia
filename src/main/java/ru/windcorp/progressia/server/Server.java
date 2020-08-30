package ru.windcorp.progressia.server;

import ru.windcorp.jputil.functions.ThrowingRunnable;
import ru.windcorp.progressia.common.util.TaskQueue;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.comms.ClientManager;
import ru.windcorp.progressia.server.world.Changer;
import ru.windcorp.progressia.server.world.ImplementedChangeTracker;
import ru.windcorp.progressia.server.world.WorldLogic;

public class Server {
	
	public static Server getCurrentServer() {
		return ServerThread.getCurrentServer();
	}
	
	private final WorldLogic world;
	private final ImplementedChangeTracker adHocChanger =
			new ImplementedChangeTracker();
	
	private final ServerThread serverThread;
	
	private final ClientManager clientManager = new ClientManager(this);
	
	private final TaskQueue taskQueue = new TaskQueue(this::isServerThread);
	
	public Server(WorldData world) {
		this.world = new WorldLogic(world);
		this.serverThread = new ServerThread(this);
	}
	
	public WorldLogic getWorld() {
		return world;
	}
	
	/**
	 * Do not use in ticks
	 */
	public Changer getAdHocChanger() {
		return adHocChanger;
	}
	
	public ClientManager getClientManager() {
		return clientManager;
	}
	
	public boolean isServerThread() {
		return getCurrentServer() == this;
	}
	
	public void invokeLater(Runnable task) {
		taskQueue.invokeLater(task);
	}

	public void invokeNow(Runnable task) {
		taskQueue.invokeNow(task);
	}

	public <E extends Exception> void waitAndInvoke(
			ThrowingRunnable<E> task
	) throws InterruptedException, E {
		taskQueue.waitAndInvoke(task);
	}

	public void start() {
		this.serverThread.start();
	}
	
	public void tick() {
		taskQueue.runTasks();
		adHocChanger.applyChanges(this);
	}
	
	public void shutdown(String message) {
		// Do nothing
	}

}
