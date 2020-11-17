package ru.windcorp.progressia.server.comms.controls;

import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

public class ControlLogicRegistry extends NamespacedInstanceRegistry<ControlLogic> {
	
	private static final ControlLogicRegistry INSTANCE =
			new ControlLogicRegistry();
	
	public static ControlLogicRegistry getInstance() {
		return INSTANCE;
	}

}
