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

package ru.windcorp.progressia.common.hacks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.util.concurrent.MoreExecutors;

import ru.windcorp.progressia.common.util.crash.CrashReports;

/**
 * This class had to be written because there is not legal way to instantiate a
 * non-async {@link EventBus} with both a custom identifier and a custom
 * exception handler. Which is a shame. Guava maintainers know about the issue
 * but have rejected solutions multiple times <em>without a clearly stated
 * reason</em>; looks like some dirty reflection will have to do.
 * 
 * @author javapony
 */
public class GuavaEventBusHijacker {

	public static final Constructor<EventBus> THE_CONSTRUCTOR;
	public static final Method DISPATCHER__PER_THREAD_DISPATCH_QUEUE;

	static {
		try {
			Class<?> dispatcherClass = Class.forName("com.google.common.eventbus.Dispatcher");

			THE_CONSTRUCTOR = EventBus.class.getDeclaredConstructor(String.class, Executor.class, dispatcherClass,
					SubscriberExceptionHandler.class);
			THE_CONSTRUCTOR.setAccessible(true);

			DISPATCHER__PER_THREAD_DISPATCH_QUEUE = dispatcherClass.getDeclaredMethod("perThreadDispatchQueue");
			DISPATCHER__PER_THREAD_DISPATCH_QUEUE.setAccessible(true);
		} catch (Exception e) {
			throw CrashReports.report(e,
					"Something went horribly wrong when setting up EventBus hijacking. Has Guava updated?");
		}
	}

	public static EventBus newEventBus(String identifier, SubscriberExceptionHandler exceptionHandler) {
		try {
			return THE_CONSTRUCTOR.newInstance(identifier, MoreExecutors.directExecutor(),
					DISPATCHER__PER_THREAD_DISPATCH_QUEUE.invoke(null), exceptionHandler);
		} catch (Exception e) {
			throw CrashReports.report(e, "Something went horribly wrong when hijacking EventBus. Has Guava updated?");
		}
	}

}
