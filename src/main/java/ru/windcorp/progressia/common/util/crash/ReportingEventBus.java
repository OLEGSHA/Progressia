package ru.windcorp.progressia.common.util.crash;

import com.google.common.eventbus.EventBus;

import ru.windcorp.progressia.common.hacks.GuavaEventBusHijacker;

public class ReportingEventBus {
	
	private ReportingEventBus() {}

	public static EventBus create(String identifier) {
		return GuavaEventBusHijacker.newEventBus(identifier, (throwable, context) -> {
			// Makes sense to append identifier to messageFormat because different EventBuses are completely unrelated
			throw CrashReports.crash(throwable, "Unexpected exception in EventBus " + identifier);
		});
	}

}
