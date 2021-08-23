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

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;

import com.google.common.eventbus.EventBus;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.jputil.functions.ThrowingRunnable;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.TaskQueue;
import ru.windcorp.progressia.common.util.crash.ReportingEventBus;
import ru.windcorp.progressia.common.world.ChunkDataListener;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.common.world.GravityModelRegistry;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.AxisRotations;
import ru.windcorp.progressia.server.comms.ClientManager;
import ru.windcorp.progressia.server.events.ServerEvent;
import ru.windcorp.progressia.server.management.load.ChunkRequestDaemon;
import ru.windcorp.progressia.server.management.load.EntityRequestDaemon;
import ru.windcorp.progressia.server.management.load.LoadManager;
import ru.windcorp.progressia.server.world.DefaultWorldLogic;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerWorldContext;
import ru.windcorp.progressia.server.world.context.impl.DefaultServerContext;
import ru.windcorp.progressia.server.world.context.impl.ReportingServerContext;
import ru.windcorp.progressia.server.world.context.impl.RotatingServerContext;
import ru.windcorp.progressia.server.world.generation.WorldGenerator;
import ru.windcorp.progressia.server.world.tasks.WorldAccessor;
import ru.windcorp.progressia.server.world.ticking.Change;
import ru.windcorp.progressia.server.world.ticking.Evaluation;
import ru.windcorp.progressia.server.world.ticking.TickerCoordinator;
import ru.windcorp.progressia.test.TestEntityDataFallingBlock;
import ru.windcorp.progressia.test.TestEntityLogicFallingBlock;

public class Server {

	/**
	 * Returns the {@link Server} instance whose main thread is the current
	 * thread.
	 * 
	 * @return the server that operates in this thread
	 */
	public static Server getCurrentServer() {
		return ServerThread.getCurrentServer();
	}

	private final DefaultWorldLogic world;
	private final WorldAccessor worldAccessor = new WorldAccessor(this);

	private final ServerThread serverThread;

	private final ClientManager clientManager;
	private final PlayerManager playerManager;
	private final LoadManager loadManager;

	private final TaskQueue taskQueue = new TaskQueue(this::isServerThread);

	private final EventBus eventBus = ReportingEventBus.create("ServerEvents");

	private final TickingSettings tickingSettings = new TickingSettings();

	public Server(DefaultWorldData world, Function<Server, WorldGenerator> generatorCreator) {
		world.addListener(new WorldDataListener() {
			@Override
			public void onChunkLoaded(DefaultWorldData world, DefaultChunkData chunk) {
				//PlanetGenerator.this.planet;
				//LogManager.getLogger().info("Loaded chunk");
				GravityModelRegistry.getInstance().get("Test:PlanetGravityModel");
				chunk.addListener(new ChunkDataListener() { // Falling Block
															// spawning logic
					@Override
					public void onChunkBlockChanged(DefaultChunkData chunk_2, Vec3i blockInChunk, BlockData previous,
							BlockData current) {
						Vec3i chunkWorldPos = new Vec3i(0,0,0);
						Coordinates.getInWorld(chunk_2.getPosition(), blockInChunk, chunkWorldPos); 
						
						/*List<Vec3i> underBlocks = getGoodCardinals(fallBlock.getUpVector().negate_());
						
						boolean notSupported = false;
						for (Vec3i v3 : underBlocks)
						{
							Vec3i inWorld = occupiedBlock.sub_(v3); 
							if (context.getBlock(inWorld).getId()=="Test:Air") {
								notSupported=true;
								break;
							}
						}*/
						
						//chunk.getPosition().mul_(16).add_(blockInChunk);
						//LogManager.getLogger().info("Put block {} at {}<{}<{}",current.getId(),chunkWorldPos.x,chunkWorldPos.y,chunkWorldPos.z);
						
						if (TestEntityLogicFallingBlock.FallingBlocks
								.contains(chunk_2.getWorld().getBlock(chunkWorldPos.add_(0, 0, 1)).getId())) {
							chunk_2.getWorld().setBlock(chunkWorldPos.add_(0, 0, 1), BlockDataRegistry.getInstance()
									.get(chunk_2.getWorld().getBlock(chunkWorldPos.add_(0, 0, 1)).getId()), true);
						}
						if (!TestEntityLogicFallingBlock.FallingBlocks.contains(current.getId())) {
							return;
						}
						//LogManager.getLogger().info("Cont");
						if (chunk_2.getWorld().getBlock(chunkWorldPos.add_(0, 0, -1)).getId() == "Test:Air") {
							LogManager.getLogger().info("Inserting FallingBlock {},{},{}",
								chunkWorldPos.x,chunkWorldPos.y,chunkWorldPos.z);

							TestEntityDataFallingBlock fallingBlock = new TestEntityDataFallingBlock(current);

							Vec3i worldPos = chunk_2.getPosition().mul_(16).add_(blockInChunk);
							Vec3 floatWorldPos = new Vec3(worldPos.x, worldPos.y, worldPos.z);
							fallingBlock.setPosition(floatWorldPos);

							fallingBlock.setEntityId(("Test:FallingBlock" + floatWorldPos.toString()
									+ String.valueOf(new Random().nextFloat())).hashCode());

							chunk.getWorld().addEntity(fallingBlock);
							//invokeLater(() -> world.addEntity(fallingBlock));

							//chunk.setBlock(blockInChunk, previous, false);
							//invokeLater(() -> world.setBlock(chunkWorldPos, BlockDataRegistry.getInstance().get("Test:Air"), false));
							

							//LogManager.getLogger().info(String.valueOf(chunkWorldPos.x) + " "
							//		+ String.valueOf(chunkWorldPos.y) + " " + String.valueOf(chunkWorldPos.z));
						}
					}
				});
			}
		});
		this.world = new DefaultWorldLogic(
			world,
			this,
			generatorCreator.apply(this),
			worldAccessor
		);
		this.serverThread = new ServerThread(this);

		this.clientManager = new ClientManager(this);
		this.playerManager = new PlayerManager(this);
		this.loadManager = new LoadManager(this);

		schedule(new ChunkRequestDaemon(loadManager.getChunkManager())::tick);
		schedule(new EntityRequestDaemon(loadManager.getEntityManager())::tick);

		// Must run after request daemons so it only schedules chunks that
		// hadn't unloaded
		schedule(this::scheduleWorldTicks);
	}

	/**
	 * Returns this server's world.
	 * 
	 * @return this server's {@link DefaultWorldLogic}
	 */
	public DefaultWorldLogic getWorld() {
		return world;
	}

	/**
	 * Instantiates and returns an new {@link ServerWorldContext} instance
	 * suitable for read and write access to the server's world. This context
	 * uses the absolute coordinate space (not rotated to match positive Z =
	 * up).
	 * 
	 * @return the context
	 * @see #createContext(AbsFace)
	 */
	public ServerWorldContext createAbsoluteContext() {
		return doCreateAbsoluteContext();
	}

	private ServerTileContext doCreateAbsoluteContext() {
		return new ReportingServerContext(DefaultServerContext.empty().inRealWorldOf(this).build())
			.withListener(worldAccessor).setPassToParent(false);
	}

	/**
	 * Instantiates and returns an new {@link ServerWorldContext} instance
	 * suitable for read and write access to the server's world. This is the
	 * preferred way to query or change the world. This context uses the
	 * coordinate space in which positive Z = {@code up}.
	 * 
	 * @param up the desired up direction
	 * @return the context
	 * @see #createContext(Vec3i)
	 * @see #createAbsoluteContext()
	 */
	public ServerWorldContext createContext(AbsFace up) {
		return new RotatingServerContext(doCreateAbsoluteContext(), up);
	}

	/**
	 * Instantiates and returns an new {@link ServerBlockContext} instance
	 * suitable for read and write access to the server's world. The context is
	 * initialized to point to the provided block. This is the preferred way to
	 * query or change the world. This context uses the coordinate space in
	 * which positive Z matches the discrete up direction of the provided
	 * location.
	 * 
	 * @param up the desired up direction
	 * @return the context
	 * @see #createContext(AbsFace)
	 * @see #createAbsoluteContext()
	 */
	public ServerBlockContext createContext(Vec3i blockInWorld) {
		AbsFace up = getWorld().getUp(blockInWorld);
		Vec3i relativeBlockInWorld = AxisRotations.relativize(blockInWorld, up, null);
		return new RotatingServerContext(doCreateAbsoluteContext(), up).push(relativeBlockInWorld);
	}

	/**
	 * Returns this server's {@link ClientManager}. Use this to deal with
	 * communications, e.g. send packets.
	 * 
	 * @return the {@link ClientManager} that handles this server
	 */
	public ClientManager getClientManager() {
		return clientManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public LoadManager getLoadManager() {
		return loadManager;
	}

	/**
	 * Checks if this thread is the main thread of this server.
	 * 
	 * @return {@code true} iff the invocation occurs in server main thread
	 */
	public boolean isServerThread() {
		return getCurrentServer() == this;
	}

	/**
	 * Requests that the provided task is executed once on next server tick. The
	 * task will be run in the main server thread. The task object is discarded
	 * after execution.
	 * <p>
	 * Use this method to request a one-time (rare) action that must necessarily
	 * happen in the main server thread, such as initialization tasks or
	 * reconfiguration.
	 * 
	 * @param task the task to run
	 * @see #invokeNow(Runnable)
	 * @see #schedule(Consumer)
	 */
	public void invokeLater(Runnable task) {
		taskQueue.invokeLater(task);
	}

	/**
	 * Executes the tasks in the server main thread as soon as possible.
	 * <p>
	 * If this method is invoked in the server main thread, then the task is run
	 * immediately (the method blocks until the task finishes). Otherwise this
	 * method behaves exactly like {@link #invokeLater(Runnable)}.
	 * <p>
	 * Use this method to make sure that a piece of code is run in the main
	 * server thread.
	 * 
	 * @param task the task to run
	 * @see #invokeLater(Runnable)
	 * @see #schedule(Consumer)
	 */
	public void invokeNow(Runnable task) {
		taskQueue.invokeNow(task);
	}

	public <E extends Exception> void waitAndInvoke(ThrowingRunnable<E> task) throws InterruptedException, E {
		taskQueue.waitAndInvoke(task);
	}

	public void schedule(Runnable task) {
		taskQueue.schedule(task);
	}

	public void schedule(Consumer<Server> task) {
		schedule(() -> task.accept(this));
	}

	/**
	 * Delayed
	 */
	public void scheduleChange(Change change) {
		serverThread.getTicker().requestChange(change);
	}
	
	/**
	 * Delayed
	 */
	public void scheduleEvaluation(Evaluation evaluation) {
		serverThread.getTicker().requestEvaluation(evaluation);
	}
	
	/**
	 * Immediate if possible, otherwise delayed
	 */
	public void requestChange(Change change) {
		if (serverThread.getTicker().getPhase() == TickerCoordinator.TickPhase.SYNCHRONOUS) {
			change.affect(this);
		} else {
			serverThread.getTicker().requestChange(change);
		}
	}

	/**
	 * Immediate if possible, otherwise delayed
	 */
	public void requestEvaluation(Evaluation evaluation) {
		if (serverThread.getTicker().getPhase() == TickerCoordinator.TickPhase.SYNCHRONOUS) {
			evaluation.evaluate(this);
		} else {
			serverThread.getTicker().requestEvaluation(evaluation);
		}
	}

	public void subscribe(Object object) {
		eventBus.register(object);
	}

	public void unsubscribe(Object object) {
		eventBus.unregister(object);
	}

	public void postEvent(ServerEvent event) {
		event.setServer(this);
		eventBus.post(event);
		event.setServer(null);
	}

	/**
	 * Returns the duration of the last server tick. Server logic should assume
	 * that this much in-world time has passed.
	 * 
	 * @return the length of the last server tick
	 */
	public double getTickLength() {
		return this.serverThread.getTicker().getTickLength();
	}

	public double getTPS() {
		return this.serverThread.getTicker().getTPS();
	}

	/**
	 * Returns the amount of ticks performed since the server has started. This
	 * value resets on shutdowns. The counter is incremented at the end of a
	 * tick.
	 * 
	 * @return the number of times the world has finished a tick since the
	 *         server has started.
	 */
	public long getUptimeTicks() {
		return this.serverThread.getTicker().getUptimeTicks();
	}

	/**
	 * Returns the ticking settings for this server.
	 * 
	 * @return a {@link TickingSettings} object
	 */
	public TickingSettings getTickingSettings() {
		return tickingSettings;
	}

	public float getLoadDistance(Player player) {
		return Units.get(100.5f, "m");
	}

	/**
	 * Starts the server. This method blocks until the server enters normal
	 * operation or fails to start.
	 */
	public void start() {
		this.serverThread.start();
	}

	/**
	 * Performs the tasks from tasks queues and repeating tasks.
	 */
	public void tick() {
		taskQueue.runTasks();
	}

	/**
	 * Shuts the server down, disconnecting the clients with the provided
	 * message. This method blocks until the shutdown is complete.
	 * 
	 * @param message the message to send to the clients as the disconnect
	 *                reason
	 */
	public void shutdown(String message) {
		LogManager.getLogger().warn("Server.shutdown() is not yet implemented");
		serverThread.stop();
	}

	private void scheduleWorldTicks(Server server) {
		server.getWorld().getChunks().forEach(chunk -> scheduleEvaluation(chunk.getTickTask()));
		scheduleEvaluation(server.getWorld().getTickEntitiesTask());
	}

	/**
	 * Returns an instance of {@link java.util.Random Random} that can be used
	 * as a source of indeterministic randomness. World generation and other
	 * algorithms that must have random but reproducible results should not use
	 * this.
	 * 
	 * @return a thread-safe indeterministic instance of
	 *         {@link java.util.Random}.
	 */
	public java.util.Random getAdHocRandom() {
		return java.util.concurrent.ThreadLocalRandom.current();
	}

}
