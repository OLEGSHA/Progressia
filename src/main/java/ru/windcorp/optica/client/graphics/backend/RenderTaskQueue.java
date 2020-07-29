/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.optica.client.graphics.backend;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ru.windcorp.optica.common.util.ThrowingRunnable;

public class RenderTaskQueue {
	
	private static final Queue<Runnable> QUEUE = new ConcurrentLinkedQueue<>();
	
	private RenderTaskQueue() {}
	
	public static void invokeLater(Runnable task) {
		QUEUE.add(task);
	}
	
	public static void invokeNow(Runnable task) {
		if (GraphicsInterface.isRenderThread()) {
			task.run();
		} else {
			invokeLater(task);
		}
	}
	
	private static final Object WAIT_AND_INVOKE_MONITOR = new Object();
	
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> void waitAndInvoke(
			ThrowingRunnable<T> task
	 ) throws InterruptedException, T {
		
		if (GraphicsInterface.isRenderThread()) {
			task.run();
			return;
		}
		
		final AtomicBoolean flag =
				new AtomicBoolean(false);
		final AtomicReference<Throwable> thrownContainer =
				new AtomicReference<>(null);
		
		invokeLater(() -> {
			
			try {
				task.run();
			} catch (Throwable t) {
				thrownContainer.set(t);
			}
			
			flag.set(true);
			
			synchronized (WAIT_AND_INVOKE_MONITOR) {
				WAIT_AND_INVOKE_MONITOR.notifyAll();
			}
		});
		
		while (!flag.get()) {
			synchronized (WAIT_AND_INVOKE_MONITOR) {
				WAIT_AND_INVOKE_MONITOR.wait();
			}
		}
		
		Throwable thrown = thrownContainer.get();
		if (thrown != null) {
			if (thrown instanceof RuntimeException) {
				throw (RuntimeException) thrown;
			}
			
			if (thrown instanceof Error) {
				throw (Error) thrown;
			}
			
			throw (T) thrown; // Guaranteed
		}
	}
	
	public static void runTasks() {
		Iterator<Runnable> tasks = QUEUE.iterator();
		
		while (tasks.hasNext()) {
			tasks.next().run();
			tasks.remove();
		}
	}

}
