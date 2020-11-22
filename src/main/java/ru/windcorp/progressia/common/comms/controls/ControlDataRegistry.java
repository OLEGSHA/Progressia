package ru.windcorp.progressia.common.comms.controls;

import ru.windcorp.progressia.common.util.namespaces.NamespacedFactoryRegistry;

public class ControlDataRegistry extends NamespacedFactoryRegistry<ControlData> {
	
	private static final ControlDataRegistry INSTANCE = new ControlDataRegistry();
	
	public static ControlDataRegistry getInstance() {
		return INSTANCE;
	}

}
