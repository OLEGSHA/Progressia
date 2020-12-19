package ru.windcorp.progressia.test;

import static ru.windcorp.progressia.client.world.block.BlockRenderRegistry.getBlockTexture;
import static ru.windcorp.progressia.client.world.tile.TileRenderRegistry.getTileTexture;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import glm.Glm;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.audio.SoundEffect;
import ru.windcorp.progressia.client.comms.controls.*;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.client.graphics.world.Selection;
import ru.windcorp.progressia.client.world.block.*;
import ru.windcorp.progressia.client.world.entity.*;
import ru.windcorp.progressia.client.world.tile.*;
import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.comms.controls.*;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.state.StatefulObjectRegistry.Factory;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.*;
import ru.windcorp.progressia.common.world.entity.*;
import ru.windcorp.progressia.common.world.tile.*;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.controls.*;
import ru.windcorp.progressia.server.world.block.*;
import ru.windcorp.progressia.server.world.entity.*;
import ru.windcorp.progressia.server.world.tile.*;

public class TestContent {
	
	public static void registerContent() {
		registerWorldContent();
		regsiterControls();
		registerMisc();
	}

	private static void registerWorldContent() {
		registerBlocks();
		registerTiles();
		registerEntities();
	}

	private static void registerBlocks() {
		register(new BlockData("Test:Air") {
				@Override
				public CollisionModel getCollisionModel() {
					return null;
				}
		});
		register(new BlockRenderNone("Test:Air"));
		register(new TestBlockLogicAir("Test:Air"));

		register(new BlockData("Test:Dirt"));
		register(new BlockRenderOpaqueCube("Test:Dirt", getBlockTexture("dirt")));
		register(new BlockLogic("Test:Dirt"));

		register(new BlockData("Test:Stone"));
		register(new BlockRenderOpaqueCube("Test:Stone", getBlockTexture("stone")));
		register(new BlockLogic("Test:Stone"));

		register(new BlockData("Test:Compass"));
		register(new BlockRenderOpaqueCube("Test:Compass", getBlockTexture("compass")));
		register(new BlockLogic("Test:Compass"));
		
		register(new BlockData("Test:Glass"));
		register(new BlockRenderTransparentCube("Test:Glass", getBlockTexture("glass_clear")));
		register(new BlockLogic("Test:Glass"));
	}

	private static void registerTiles() {
		register(new TileData("Test:Grass"));
		register(new TileRenderGrass("Test:Grass", getTileTexture("grass_top"), getTileTexture("grass_side")));
		register(new TestTileLogicGrass("Test:Grass"));
		
		register(new TileData("Test:Stones"));
		register(new TileRenderSimple("Test:Stones", getTileTexture("stones")));
		register(new HangingTileLogic("Test:Stones"));
		
		register(new TileData("Test:YellowFlowers"));
		register(new TileRenderSimple("Test:YellowFlowers", getTileTexture("yellow_flowers")));
		register(new HangingTileLogic("Test:YellowFlowers"));
		
		register(new TileData("Test:Sand"));
		register(new TileRenderSimple("Test:Sand", getTileTexture("sand")));
		register(new HangingTileLogic("Test:Sand"));
	}

	private static void registerEntities() {
		float scale = 1.8f / 8;
		registerEntityData("Test:Player", e -> e.setCollisionModel(new AABB(0, 0, 4*scale, 0.8f, 0.8f, 1.8f)));
		register(new TestEntityRenderHuman("Test:Player"));
		register(new EntityLogic("Test:Player"));
		
		register("Test:Statie", TestEntityDataStatie::new);
		register(new TestEntityRenderStatie("Test:Statie"));
		register(new TestEntityLogicStatie("Test:Statie"));
	}

	private static void regsiterControls() {
		ControlDataRegistry data = ControlDataRegistry.getInstance();
		ControlTriggerRegistry triggers = ControlTriggerRegistry.getInstance();
		ControlLogicRegistry logic = ControlLogicRegistry.getInstance();
		
		data.register("Test:Switch000", ControlData::new);
		triggers.register(ControlTriggers.of(
				"Test:Switch000",
				KeyEvent.class,
				KeyMatcher.of(GLFW.GLFW_KEY_H).matcher()
		));
		logic.register(ControlLogic.of("Test:Switch000", (server, packet, client) -> {
			Vec3i z000 = new Vec3i(0, 0, 0);
			
			ChunkData chunk = server.getWorld().getChunk(z000).getData();
			
			BlockData block;
			if (chunk.getBlock(z000).getId().equals("Test:Stone")) {
				block = BlockDataRegistry.getInstance().get("Test:Glass");
			} else {
				block = BlockDataRegistry.getInstance().get("Test:Stone");
			}
			
			server.getWorldAccessor().setBlock(z000, block);
		}));
		
		data.register("Test:BreakBlock", ControlBreakBlockData::new);
		triggers.register(ControlTriggers.of(
				"Test:BreakBlock",
				KeyEvent.class,
				TestContent::onBlockBreakTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_LEFT).matcher(),
				i -> getSelection().exists()
		));
		logic.register(ControlLogic.of("Test:BreakBlock", TestContent::onBlockBreakReceived));
		
		data.register("Test:PlaceBlock", ControlPlaceBlockData::new);
		triggers.register(ControlTriggers.of(
				"Test:PlaceBlock",
				KeyEvent.class,
				TestContent::onBlockPlaceTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_RIGHT).matcher(),
				i -> getSelection().exists()
		));
		logic.register(ControlLogic.of("Test:PlaceBlock", TestContent::onBlockPlaceReceived));
	}
	
	private static void register(BlockData x) {
		BlockDataRegistry.getInstance().register(x);
	}
	
	private static void register(TileData x) {
		TileDataRegistry.getInstance().register(x);
	}
	
	private static void register(
			String id,
			Factory<EntityData> factory
	) {
		EntityDataRegistry.getInstance().register(id, factory);
	}
	
	private static void registerEntityData(
			String id,
			Consumer<EntityData> transform
	) {
		EntityDataRegistry.getInstance().register(id, new Factory<EntityData>() {
			@Override
			public EntityData build() {
				EntityData entity = new EntityData(id);
				transform.accept(entity);
				return entity;
			}
		});
	}
	
	private static void register(BlockRender x) {
		BlockRenderRegistry.getInstance().register(x);
	}
	
	private static void register(TileRender x) {
		TileRenderRegistry.getInstance().register(x);
	}
	
	private static void register(EntityRender x) {
		EntityRenderRegistry.getInstance().register(x);
	}
	
	private static void register(BlockLogic x) {
		BlockLogicRegistry.getInstance().register(x);
	}
	
	private static void register(TileLogic x) {
		TileLogicRegistry.getInstance().register(x);
	}
	
	private static void register(EntityLogic x) {
		EntityLogicRegistry.getInstance().register(x);
	}
	
	private static Selection getSelection() {
		ru.windcorp.progressia.client.Client client = ClientState.getInstance();
		if (client == null) return null;
		
		LocalPlayer player = client.getLocalPlayer();
		if (player == null) return null;
		
		return player.getSelection();
	}
	
	private static void onBlockBreakTrigger(ControlData control) {
		((ControlBreakBlockData) control).setBlockInWorld(getSelection().getBlock());
		SoundEffect sfx = new SoundEffect("Progressia:BlockDestroy");
		sfx.setPosition(getSelection().getPoint());
		sfx.setPitch((float) (Math.random() + 1 * 0.5));
		sfx.play(false);
	}
	
	private static void onBlockBreakReceived(Server server, PacketControl packet, ru.windcorp.progressia.server.comms.Client client) {
		Vec3i blockInWorld = ((ControlBreakBlockData) packet.getControl()).getBlockInWorld();
		server.getWorldAccessor().setBlock(blockInWorld, BlockDataRegistry.getInstance().get("Test:Air"));
	}
	
	private static void onBlockPlaceTrigger(ControlData control) {
		((ControlPlaceBlockData) control).setBlockInWorld(
				getSelection().getBlock().add_(getSelection().getSurface().getVector())
		);
	}
	
	private static void onBlockPlaceReceived(Server server, PacketControl packet, ru.windcorp.progressia.server.comms.Client client) {
		Vec3i blockInWorld = ((ControlPlaceBlockData) packet.getControl()).getBlockInWorld();
		if (server.getWorld().getData().getChunkByBlock(blockInWorld) == null) return;
		server.getWorldAccessor().setBlock(blockInWorld, BlockDataRegistry.getInstance().get("Test:Stone"));
	}

	public static void generateChunk(ChunkData chunk) {
		final int bpc = ChunkData.BLOCKS_PER_CHUNK;
		
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData stone = BlockDataRegistry.getInstance().get("Test:Stone");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
	
		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");
		TileData stones = TileDataRegistry.getInstance().get("Test:Stones");
		TileData flowers = TileDataRegistry.getInstance().get("Test:YellowFlowers");
		TileData sand = TileDataRegistry.getInstance().get("Test:Sand");
	
		Vec3i aPoint = new Vec3i(5, 0, bpc + bpc/4).sub(chunk.getPosition().mul_(ChunkData.BLOCKS_PER_CHUNK));
		Vec3i pos = new Vec3i();
		
		for (int x = 0; x < bpc; ++x) {
			for (int y = 0; y < bpc; ++y) {
				for (int z = 0; z < bpc; ++z) {
					
					pos.set(x, y, z);
					float f = aPoint.sub(pos, pos).length();
					pos.set(x, y, z);
					
					if (f > 17) {
						chunk.setBlock(pos, stone, false);
					} else if (f > 14) {
						chunk.setBlock(pos, dirt, false);
					} else {
						chunk.setBlock(pos, air, false);
					}
					
				}
			}
		}
		
		for (int x = 0; x < bpc; ++x) {
			for (int y = 0; y < bpc; ++y) {
				pos.set(x, y, 0);
				
				for (pos.z = bpc - 1; pos.z >= 0 && chunk.getBlock(pos) == air; --pos.z);
				if (pos.z < 0) continue;
				
				chunk.getTiles(pos, BlockFace.TOP).add(grass);
				for (BlockFace face : BlockFace.getFaces()) {
					if (face.getVector().z != 0) continue;
					pos.add(face.getVector());
					
					if (!ChunkData.isInBounds(pos) || (chunk.getBlock(pos) == air)) {
						pos.sub(face.getVector());
						chunk.getTiles(pos, face).add(grass);
					} else {
						pos.sub(face.getVector());
					}
				}
				
				int hash = x*x * 19 ^ y*y * 41 ^ pos.z*pos.z * 147;
				if (hash % 5 == 0) {
					chunk.getTiles(pos, BlockFace.TOP).addFarthest(sand);
				}
				
				hash = x*x * 13 ^ y*y * 37 ^ pos.z*pos.z * 129;
				if (hash % 5 == 0) {
					chunk.getTiles(pos, BlockFace.TOP).addFarthest(stones);
				}
				
				hash = x*x * 17 ^ y*y * 39 ^ pos.z*pos.z * 131;
				if (hash % 9 == 0) {
					chunk.getTiles(pos, BlockFace.TOP).addFarthest(flowers);
				}
			}
		}
		
		if (Glm.equals(chunk.getPosition(), Vectors.ZERO_3i)) {
			EntityData player = EntityDataRegistry.getInstance().create("Test:Player");
			player.setEntityId(0x42);
			player.setPosition(new Vec3(-6, -6, 20));
			player.setDirection(new Vec2(
					(float) Math.toRadians(40), (float) Math.toRadians(45)
			));
			chunk.getEntities().add(player);
			
			EntityData statie = EntityDataRegistry.getInstance().create("Test:Statie");
			statie.setEntityId(0xDEADBEEF);
			statie.setPosition(new Vec3(0, 15, 16));
			chunk.getEntities().add(statie);
		}
	}

	private static void registerMisc() {
		ChunkIO.registerCodec(new TestChunkCodec());
	}

}
