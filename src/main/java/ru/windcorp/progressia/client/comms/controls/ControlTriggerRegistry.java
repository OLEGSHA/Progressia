package ru.windcorp.progressia.client.comms.controls;

import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

public class ControlTriggerRegistry extends NamespacedInstanceRegistry<ControlTrigger> {
	
	private static final ControlTriggerRegistry INSTANCE =
			new ControlTriggerRegistry();
	
	public static ControlTriggerRegistry getInstance() {
		return INSTANCE;
	}

}
