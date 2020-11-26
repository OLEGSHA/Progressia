package ru.windcorp.progressia.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import ru.windcorp.jputil.functions.ThrowingRunnable;
import ru.windcorp.progressia.common.util.TaskQueue;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.comms.ClientManager;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.tasks.WorldAccessor;
import ru.windcorp.progressia.server.world.ticking.Change;
import ru.windcorp.progressia.server.world.ticking.Evaluation;

public class Server {
	
	public static Server getCurrentServer() {
		return ServerThread.getCurrentServer();
	}
	
	private final WorldLogic world;
	private final WorldAccessor worldAccessor = new WorldAccessor(this);
	
	private final ServerThread serverThread;
	
	private final ClientManager clientManager = new ClientManager(this);
	
	private final TaskQueue taskQueue = new TaskQueue(this::isServerThread);
	private final Collection<Consumer<Server>> repeatingTasks = Collections.synchronizedCollection(new ArrayList<>());
	
	public Server(WorldData world) {
		this.world = new WorldLogic(world, this);
		this.serverThread = new ServerThread(this);
		
		invokeEveryTick(this::scheduleChunkTicks);
	}
	
	public WorldLogic getWorld() {
		return world;
	}
	
	/**
	 * Returns this server's {@link ClientManager}.
	 * Use this to deal with communications, e.g. send packets.
	 * @return the {@link ClientManager} that handles this server
	 */
	public ClientManager getClientManager() {
		return clientManager;
	}
	
	/**
	 * Checks if this thread is the main thread of this server.
	 * @return {@code true} iff the invocation occurs in server main thread
	 */
	public boolean isServerThread() {
		return getCurrentServer() == this;
	}
	
	/**
	 * Requests that the provided task is executed once on next server tick.
	 * The task will be run in the main server thread. The task object is
	 * discarded after execution.
	 * 
	 * <p>Use this method to request a one-time (rare) action that must necessarily
	 * happen in the main server thread, such as initialization tasks or reconfiguration.
	 * 
	 * @param task the task to run
	 * @see #invokeNow(Runnable)
	 * @see #invokeEveryTick(Consumer)
	 */
	public void invokeLater(Runnable task) {
		taskQueue.invokeLater(task);
	}

	/**
	 * Executes the tasks in the server main thread as soon as possible.
	 * 
	 * <p>If this method is invoked in the server main thread, then the task is
	 * run immediately (the method blocks until the task finishes). Otherwise
	 * this method behaves exactly like {@link #invokeLater(Runnable)}.
	 * 
	 * <p>Use this method to make sure that a piece of code is run in the main server
	 * thread.
	 * 
	 * @param task the task to run
	 * @see #invokeLater(Runnable)
	 * @see #invokeEveryTick(Consumer)
	 */
	public void invokeNow(Runnable task) {
		taskQueue.invokeNow(task);
	}

	public <E extends Exception> void waitAndInvoke(
			ThrowingRunnable<E> task
	) throws InterruptedException, E {
		taskQueue.waitAndInvoke(task);
	}
	
	public void invokeEveryTick(Consumer<Server> task) {
		repeatingTasks.add(task);
	}
	
	public void requestChange(Change change) {
		serverThread.getTicker().requestChange(change);
	}
	
	public void requestEvaluation(Evaluation evaluation) {
		serverThread.getTicker().requestEvaluation(evaluation);
	}
	
	public double getTickLength() {
		return this.serverThread.getTicker().getTickLength();
	}
	
	public WorldAccessor getWorldAccessor() {
		return worldAccessor;
	}

	public void start() {
		this.serverThread.start();
	}
	
	public void tick() {
		taskQueue.runTasks();
		repeatingTasks.forEach(t -> t.accept(this));
	}
	
	public void shutdown(String message) {
		// Do nothing
	}
	
	private void scheduleChunkTicks(Server server) {
		
	}

}
