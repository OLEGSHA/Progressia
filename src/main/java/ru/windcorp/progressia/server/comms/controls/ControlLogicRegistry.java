package ru.windcorp.progressia.server.comms.controls;

import ru.windcorp.progressia.common.util.namespaces.NamespacedRegistry;

public class ControlLogicRegistry extends NamespacedRegistry<ControlLogic> {
	
	private static final ControlLogicRegistry INSTANCE =
			new ControlLogicRegistry();
	
	public static ControlLogicRegistry getInstance() {
		return INSTANCE;
	}

}
