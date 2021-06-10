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

package ru.windcorp.progressia.client.graphics.backend;

import ru.windcorp.jputil.functions.ThrowingRunnable;
import ru.windcorp.progressia.common.util.TaskQueue;

public class RenderTaskQueue {

	private static final TaskQueue HANDLER = new TaskQueue(GraphicsInterface::isRenderThread);

	public static void schedule(Runnable task) {
		HANDLER.schedule(task);
	}

	public static void removeScheduled(Runnable task) {
		HANDLER.removeScheduled(task);
	}

	public static void invokeLater(Runnable task) {
		HANDLER.invokeLater(task);
	}

	public static void invokeNow(Runnable task) {
		HANDLER.invokeNow(task);
	}

	public static <E extends Exception> void waitAndInvoke(ThrowingRunnable<E> task) throws InterruptedException, E {
		HANDLER.waitAndInvoke(task);
	}

	public static void runTasks() {
		HANDLER.runTasks();
	}

}
