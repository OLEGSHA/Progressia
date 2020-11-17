package ru.windcorp.progressia.common.comms.controls;

import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

public class ControlDataRegistry extends NamespacedInstanceRegistry<ControlData> {
	
	private static final ControlDataRegistry INSTANCE = new ControlDataRegistry();
	
	public static ControlDataRegistry getInstance() {
		return INSTANCE;
	}

}
