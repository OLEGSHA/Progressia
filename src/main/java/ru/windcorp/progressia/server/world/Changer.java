package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.block.BlockData;
import ru.windcorp.progressia.common.block.BlockFace;
import ru.windcorp.progressia.common.block.TileData;

public interface Changer {

	void setBlock(Vec3i pos, BlockData block);

	void addTile(Vec3i block, BlockFace face, TileData tile);

	void removeTile(Vec3i block, BlockFace face, TileData tile);

}