package ru.windcorp.progressia.server.world.context;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.context.WorldDataContextRO;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.WorldGenericContextRO;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tile.TileLogic;

public interface ServerWorldContextRO extends WorldDataContextRO, ServerContext {

	public interface Logic extends WorldGenericContextRO<BlockLogic, TileLogic, EntityData>, ServerContext {

		/**
		 * Acquires the data view of this context. Use this method to
		 * conveniently access data content. The returned object is linked to
		 * this context and operates on the same data.
		 * 
		 * @return a view of this context that returns data objects
		 */
		ServerWorldContextRO data();
		
		@Override
		ServerBlockContextRO.Logic push(Vec3i location);
		
		@Override
		ServerTileStackContextRO.Logic push(Vec3i location, RelFace face);
		
		@Override
		ServerTileContextRO.Logic push(Vec3i location, RelFace face, int layer);

	}

	/**
	 * Acquires the logic view of this context. Use this method to conveniently
	 * access logic content. The returned object is linked to this context and
	 * operates on the same data.
	 * 
	 * @return a view of this context that returns appropriate logic objects
	 */
	ServerWorldContextRO.Logic logic();
	
	@Override
	ServerBlockContextRO push(Vec3i location);
	
	@Override
	ServerTileStackContextRO push(Vec3i location, RelFace face);
	
	@Override
	ServerTileContextRO push(Vec3i location, RelFace face, int layer);

}
