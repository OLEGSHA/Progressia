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

package ru.windcorp.progressia.common.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import ru.windcorp.jputil.functions.ThrowingRunnable;

public class TaskQueue {

	private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
	private final Collection<Runnable> repeating = new ConcurrentLinkedQueue<>();

	private final BooleanSupplier runNow;

	public TaskQueue(BooleanSupplier runNow) {
		this.runNow = runNow;
	}

	public void schedule(Runnable task) {
		repeating.add(task);
	}

	public void removeScheduled(Runnable task) {
		repeating.remove(task);
	}

	public void invokeLater(Runnable task) {
		queue.add(task);
	}

	public void invokeNow(Runnable task) {
		if (runNow.getAsBoolean()) {
			task.run();
		} else {
			invokeLater(task);
		}
	}

	private final Object waitAndInvokeMonitor = new Object();

	@SuppressWarnings("unchecked")
	public <E extends Exception> void waitAndInvoke(ThrowingRunnable<E> task) throws InterruptedException, E {

		if (runNow.getAsBoolean()) {
			task.run();
			return;
		}

		final AtomicBoolean flag = new AtomicBoolean(false);
		final AtomicReference<Throwable> thrownContainer = new AtomicReference<>(null);

		invokeLater(() -> {

			try {
				task.run();
			} catch (Throwable t) {
				thrownContainer.set(t);
			}

			flag.set(true);

			synchronized (waitAndInvokeMonitor) {
				waitAndInvokeMonitor.notifyAll();
			}
		});

		while (!flag.get()) {
			synchronized (waitAndInvokeMonitor) {
				waitAndInvokeMonitor.wait();
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

			throw (E) thrown; // Guaranteed
		}
	}

	public void runTasks() {
		Iterator<Runnable> tasks = queue.iterator();

		while (tasks.hasNext()) {
			tasks.next().run();
			tasks.remove();
		}

		for (Runnable task : repeating) {
			task.run();
		}
	}

}
