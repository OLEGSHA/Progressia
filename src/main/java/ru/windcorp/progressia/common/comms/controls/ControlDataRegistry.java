package ru.windcorp.progressia.common.comms.controls;

import ru.windcorp.progressia.common.util.NamespacedRegistry;

public class ControlDataRegistry extends NamespacedRegistry<ControlData> {
	
	private static final ControlDataRegistry INSTANCE = new ControlDataRegistry();
	
	public static ControlDataRegistry getInstance() {
		return INSTANCE;
	}

}
