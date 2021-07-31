package ru.windcorp.progressia.server.world.context;

import ru.windcorp.progressia.common.world.context.WorldDataContextRO;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.WorldGenericContextRO;
import ru.windcorp.progressia.server.world.ChunkLogicRO;
import ru.windcorp.progressia.server.world.TileLogicReferenceRO;
import ru.windcorp.progressia.server.world.TileLogicStackRO;
import ru.windcorp.progressia.server.world.WorldLogicRO;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tile.TileLogic;

public interface ServerWorldContextRO extends WorldDataContextRO, ServerContext {

	public interface Logic
		extends
		WorldGenericContextRO<BlockLogic, TileLogic, TileLogicStackRO, TileLogicReferenceRO, ChunkLogicRO, EntityData>,
		WorldLogicRO,
		ServerContext {

		/**
		 * Acquires the data view of this context. Use this method to
		 * conveniently access data content. The returned object is linked to
		 * this context and operates on the same data.
		 * 
		 * @return a view of this context that returns data objects
		 */
		ServerWorldContextRO data();

	}

	/**
	 * Acquires the logic view of this context. Use this method to conveniently
	 * access logic content. The returned object is linked to this context and
	 * operates on the same data.
	 * 
	 * @return a view of this context that returns appropriate logic objects
	 */
	ServerWorldContextRO.Logic logic();

}
