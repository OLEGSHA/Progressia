package ru.windcorp.progressia.test;

import static ru.windcorp.progressia.client.world.block.BlockRenderRegistry.getBlockTexture;
import static ru.windcorp.progressia.client.world.tile.TileRenderRegistry.getTileTexture;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import glm.vec._3.Vec3;
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
	public static final Vec3 SPAWN = new Vec3(8, 8, 880);
	
	public static final List<BlockData> PLACEABLE_BLOCKS = new ArrayList<>();
	public static final List<TileData> PLACEABLE_TILES = new ArrayList<>();
	
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
		Set<String> placeableBlacklist = new HashSet<>();
		
		register(new BlockData("Test:Air") {
				@Override
				public CollisionModel getCollisionModel() {
					return null;
				}
		});
		register(new BlockRenderNone("Test:Air"));
		register(new TestBlockLogicAir("Test:Air"));
		placeableBlacklist.add("Test:Air");

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
		
		BlockDataRegistry.getInstance().values().forEach(PLACEABLE_BLOCKS::add);
		PLACEABLE_BLOCKS.removeIf(b -> placeableBlacklist.contains(b.getId()));
		PLACEABLE_BLOCKS.sort(Comparator.comparing(BlockData::getId));
	}

	private static void registerTiles() {
		Set<String> placeableBlacklist = new HashSet<>();
		
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
		
		TileDataRegistry.getInstance().values().forEach(PLACEABLE_TILES::add);
		PLACEABLE_TILES.removeIf(b -> placeableBlacklist.contains(b.getId()));
		PLACEABLE_TILES.sort(Comparator.comparing(TileData::getId));
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
		
		data.register("Test:BreakBlock", ControlBreakBlockData::new);
		triggers.register(ControlTriggers.of(
				"Test:BreakBlock",
				KeyEvent.class,
				TestContent::onBlockBreakTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_LEFT).matcher(),
				i -> isAnythingSelected()
		));
		logic.register(ControlLogic.of("Test:BreakBlock", TestContent::onBlockBreakReceived));
		
		data.register("Test:PlaceBlock", ControlPlaceBlockData::new);
		triggers.register(ControlTriggers.of(
				"Test:PlaceBlock",
				KeyEvent.class,
				TestContent::onBlockPlaceTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_RIGHT).matcher(),
				i -> isAnythingSelected() && TestPlayerControls.getInstance().isBlockSelected()
		));
		logic.register(ControlLogic.of("Test:PlaceBlock", TestContent::onBlockPlaceReceived));
		
		data.register("Test:PlaceTile", ControlPlaceTileData::new);
		triggers.register(ControlTriggers.of(
				"Test:PlaceTile",
				KeyEvent.class,
				TestContent::onTilePlaceTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_RIGHT).matcher(),
				i -> isAnythingSelected() && !TestPlayerControls.getInstance().isBlockSelected()
		));
		logic.register(ControlLogic.of("Test:PlaceTile", TestContent::onTilePlaceReceived));
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
	
	private static boolean isAnythingSelected() {
		ru.windcorp.progressia.client.Client client = ClientState.getInstance();
		if (client == null || !client.isReady()) return false;
		
		return client.getLocalPlayer().getSelection().exists();
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
		((ControlPlaceBlockData) control).set(
				TestPlayerControls.getInstance().getSelectedBlock(),
				getSelection().getBlock().add_(getSelection().getSurface().getVector())
		);
	}
	
	private static void onBlockPlaceReceived(Server server, PacketControl packet, ru.windcorp.progressia.server.comms.Client client) {
		ControlPlaceBlockData controlData = ((ControlPlaceBlockData) packet.getControl());
		BlockData block = controlData.getBlock();
		Vec3i blockInWorld = controlData.getBlockInWorld();
		if (server.getWorld().getData().getChunkByBlock(blockInWorld) == null) return;
		server.getWorldAccessor().setBlock(blockInWorld, block);
	}
	
	private static void onTilePlaceTrigger(ControlData control) {
		((ControlPlaceTileData) control).set(
				TestPlayerControls.getInstance().getSelectedTile(),
				getSelection().getBlock(),
				getSelection().getSurface()
		);
	}
	
	private static void onTilePlaceReceived(Server server, PacketControl packet, ru.windcorp.progressia.server.comms.Client client) {
		ControlPlaceTileData controlData = ((ControlPlaceTileData) packet.getControl());
		TileData tile = controlData.getTile();
		Vec3i blockInWorld = controlData.getBlockInWorld();
		BlockFace face = controlData.getFace();
		
		if (server.getWorld().getData().getChunkByBlock(blockInWorld) == null) return;
		if (server.getWorld().getData().getTiles(blockInWorld, face).isFull()) return;
		server.getWorldAccessor().addTile(blockInWorld, face, tile);
	}

	private static void registerMisc() {
		ChunkIO.registerCodec(new TestChunkCodec());
	}

}
