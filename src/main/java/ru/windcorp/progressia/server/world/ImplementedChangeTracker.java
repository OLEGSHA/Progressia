package ru.windcorp.progressia.server.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.comms.packets.PacketWorldChange;
import ru.windcorp.progressia.common.util.LowOverheadCache;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;

public class ImplementedChangeTracker implements Changer {
	
	public static interface Change {
		void applyOnServer(WorldData world);
		Packet asPacket();
	}
	
	private static class SetBlock
	extends PacketWorldChange
	implements Change {

		private final Vec3i position = new Vec3i();
		private BlockData block;
		
		public SetBlock() {
			super("Core", "SetBlock");
		}
		
		public void initialize(Vec3i position, BlockData block) {
			this.position.set(position.x, position.y, position.z);
			this.block = block;
		}
		
		@Override
		public void applyOnServer(WorldData world) {
			Vec3i blockInChunk = Vectors.grab3i();
			Coordinates.convertInWorldToInChunk(position, blockInChunk);
			
			world.getChunkByBlock(position).setBlock(blockInChunk, block);
			
			Vectors.release(blockInChunk);
		}
		
		@Override
		public void apply(WorldData world) {
			applyOnServer(world);
		}
		
		@Override
		public Packet asPacket() {
			return this;
		}
		
	}
	
	private static class AddOrRemoveTile
	extends PacketWorldChange
	implements Change {
		
		private final Vec3i position = new Vec3i();
		private BlockFace face;
		private TileData tile;
		
		private boolean shouldAdd;
		
		public AddOrRemoveTile() {
			super("Core", "AddOrRemoveTile");
		}
		
		public void initialize(
				Vec3i position, BlockFace face,
				TileData tile,
				boolean shouldAdd
		) {
			this.position.set(position.x, position.y, position.z);
			this.face = face;
			this.tile = tile;
			this.shouldAdd = shouldAdd;
		}
		
		@Override
		public void applyOnServer(WorldData world) {
			Vec3i blockInChunk = Vectors.grab3i();
			Coordinates.convertInWorldToInChunk(position, blockInChunk);
			
			List<TileData> tiles = world.getChunkByBlock(position)
					.getTiles(blockInChunk, face);
			
			if (shouldAdd) {
				tiles.add(tile);
			} else {
				tiles.remove(tile);
			}
			
			Vectors.release(blockInChunk);
		}
		
		@Override
		public void apply(WorldData world) {
			applyOnServer(world);
		}
		
		@Override
		public Packet asPacket() {
			return this;
		}
		
	}

	private final List<Change> changes = new ArrayList<>(1024);
	
	private final LowOverheadCache<SetBlock> setBlockCache =
			new LowOverheadCache<>(SetBlock::new);
	
	private final LowOverheadCache<AddOrRemoveTile> addOrRemoveTileCache =
			new LowOverheadCache<>(AddOrRemoveTile::new);
	
	@Override
	public void setBlock(Vec3i pos, BlockData block) {
		SetBlock change = setBlockCache.grab();
		change.initialize(pos, block);
		changes.add(change);
	}
	
	@Override
	public void addTile(Vec3i block, BlockFace face, TileData tile) {
		AddOrRemoveTile change = addOrRemoveTileCache.grab();
		change.initialize(block, face, tile, true);
		changes.add(change);
	}
	
	@Override
	public void removeTile(Vec3i block, BlockFace face, TileData tile) {
		AddOrRemoveTile change = addOrRemoveTileCache.grab();
		change.initialize(block, face, tile, false);
		changes.add(change);
	}
	
	public void applyChanges(Server server) {
		changes.forEach(c -> c.applyOnServer(server.getWorld().getData()));
		changes.stream().map(Change::asPacket).filter(Objects::nonNull).forEach(
				server.getClientManager()::broadcastGamePacket
		);
		changes.clear();
	}

}
