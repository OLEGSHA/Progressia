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

package ru.windcorp.progressia.common.util.crash;

import com.google.common.eventbus.EventBus;

import ru.windcorp.progressia.common.hacks.GuavaEventBusHijacker;

/**
 * A utility for creating Guava's {@link EventBus}es that
 * {@linkplain CrashReports report} exceptions instead of suppressing them.
 * 
 * @author javapony
 */
public class ReportingEventBus {

	private ReportingEventBus() {
	}

	/**
	 * Instantiates a new {@link EventBus} with the provided identifier that
	 * reports any unhandled exceptions with {@link CrashReports}.
	 * 
	 * @param identifier the identifier of the new bus
	 * @return the created event bus
	 * @implNote This implementation relies on {@link GuavaEventBusHijacker} for
	 *           creating buses with custom identifiers and uncaught exception
	 *           handlers. It may break suddenly with a Guava update.
	 */
	public static EventBus create(String identifier) {
		return GuavaEventBusHijacker.newEventBus(
			identifier,
			(throwable, context) -> {
				// Makes sense to append identifier to messageFormat because
				// different EventBuses are completely unrelated
				throw CrashReports.crash(throwable, "Unexpected exception in EventBus " + identifier);
			}
		);
	}

}
