package ru.windcorp.progressia.client.comms.controls;

import ru.windcorp.progressia.common.util.NamespacedRegistry;

public class ControlTriggerRegistry extends NamespacedRegistry<ControlTrigger> {
	
	private static final ControlTriggerRegistry INSTANCE =
			new ControlTriggerRegistry();
	
	public static ControlTriggerRegistry getInstance() {
		return INSTANCE;
	}

}
