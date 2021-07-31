package ru.windcorp.progressia.server.world.context;

import ru.windcorp.progressia.common.world.WorldDataRO;
import ru.windcorp.progressia.server.world.WorldLogicRO;

public interface ServerWorldContextRO extends WorldDataRO, ServerContext {

	public interface Logic extends WorldLogicRO {

		ServerWorldContextRO data();

	}

	ServerWorldContextRO.Logic logic();

}
