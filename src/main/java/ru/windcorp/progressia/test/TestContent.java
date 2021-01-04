package ru.windcorp.progressia.test;

import static ru.windcorp.progressia.client.world.block.BlockRenderRegistry.getBlockTexture;
import static ru.windcorp.progressia.client.world.tile.TileRenderRegistry.getTileTexture;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.audio.SoundEffect;
import ru.windcorp.progressia.client.comms.controls.*;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.world.Selection;
import ru.windcorp.progressia.client.world.block.*;
import ru.windcorp.progressia.client.world.entity.*;
import ru.windcorp.progressia.client.world.tile.*;
import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.comms.controls.*;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.state.StatefulObjectRegistry.Factory;
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
	
	public static final String PLAYER_LOGIN = "Sasha";
	public static final long PLAYER_ENTITY_ID = 0x42;
	public static final long STATIE_ENTITY_ID = 0xDEADBEEF;
	
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

		for (String type : new String[] {"Monolith", "Cracked", "Gravel"}) {
			String id = "Test:Granite" + type;
			
			register(new BlockData(id));
			register(new BlockRenderOpaqueCube(id, getBlockTexture("granite_" + type.toLowerCase())));
			register(new BlockLogic(id));
		}

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
		
		register(new TileData("Test:SnowOpaque"));
		register(new TileRenderSimple("Test:SnowOpaque", getTileTexture("snow_opaque"), true));
		register(new HangingTileLogic("Test:SnowOpaque"));
		
		register(new TileData("Test:SnowHalf"));
		register(new TileRenderSimple("Test:SnowHalf", getTileTexture("snow_half")));
		register(new HangingTileLogic("Test:SnowHalf"));
		
		register(new TileData("Test:SnowQuarter"));
		register(new TileRenderSimple("Test:SnowQuarter", getTileTexture("snow_quarter")));
		register(new HangingTileLogic("Test:SnowQuarter"));
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
		if (client == null || !client.isReady()) return null;
		
		return client.getLocalPlayer().getSelection();
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

	private static void registerMisc() {
		ChunkIO.registerCodec(new TestChunkCodec());
	}

}
