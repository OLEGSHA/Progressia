/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.test;

import static ru.windcorp.progressia.client.world.block.BlockRenderRegistry.getBlockTexture;
import static ru.windcorp.progressia.client.world.tile.TileRenderRegistry.getTileTexture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.lwjgl.glfw.GLFW;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.audio.Sound;
import ru.windcorp.progressia.client.comms.controls.*;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.world.Selection;
import ru.windcorp.progressia.client.world.block.*;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerRegistry;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSimple;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSurface;
import ru.windcorp.progressia.client.world.entity.*;
import ru.windcorp.progressia.client.world.tile.*;
import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.comms.controls.*;
import ru.windcorp.progressia.common.state.StatefulObjectRegistry.Factory;
import ru.windcorp.progressia.common.world.GravityModelRegistry;
import ru.windcorp.progressia.common.world.block.*;
import ru.windcorp.progressia.common.world.entity.*;
import ru.windcorp.progressia.common.world.io.ChunkIO;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.tile.*;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.controls.*;
import ru.windcorp.progressia.server.world.block.*;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileStackContext;
import ru.windcorp.progressia.server.world.entity.*;
import ru.windcorp.progressia.server.world.generation.planet.PlanetGravityModel;
import ru.windcorp.progressia.server.world.tile.*;
import ru.windcorp.progressia.test.Rocks.RockType;
import ru.windcorp.progressia.test.gen.TestGravityModel;

public class TestContent {

	public static final String PLAYER_LOGIN = "Sasha";
	public static final long PLAYER_ENTITY_ID = 0x42;
	public static final long STATIE_ENTITY_ID = 0xDEADBEEF;

	public static final List<BlockData> PLACEABLE_BLOCKS = new ArrayList<>();
	public static final List<TileData> PLACEABLE_TILES = new ArrayList<>();

	public static final Rocks ROCKS = new Rocks();

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

		registerSimplestBlock("Dirt");
		registerSimplestBlock("Chernozem");
		registerSimplestBlock("Stone");
		registerSimplestBlock("Brick");
		registerSimplestBlock("BrickWhite");
		registerSimplestBlock("Sand");
		registerSimplestBlock("Concrete");
		registerSimplestBlock("WoodenPlank");
		
		registerRocks();

		register(new BlockData("Test:Glass"));
		register(new BlockRenderTransparentCube("Test:Glass", getBlockTexture("Glass")));
		register(new TestBlockLogicGlass("Test:Glass"));

		register(new BlockData("Test:Log"));
		register(
			new BlockRenderOpaqueCube(
				"Test:Log",
				getBlockTexture("LogTop"),
				getBlockTexture("LogTop"),
				getBlockTexture("LogSide")
			)
		);
		register(new BlockLogic("Test:Log"));
		
		register(new BlockData("Test:TemporaryLeaves"));
		register(new BlockRenderTransparentCube("Test:TemporaryLeaves", getBlockTexture("TemporaryLeaves")));
		// Sic, using Glass logic for leaves because  Test
		register(new TestBlockLogicGlass("Test:TemporaryLeaves")); 

		BlockDataRegistry.getInstance().values().forEach(PLACEABLE_BLOCKS::add);
		PLACEABLE_BLOCKS.removeIf(b -> placeableBlacklist.contains(b.getId()));
		PLACEABLE_BLOCKS.sort(Comparator.comparing(BlockData::getId));

	}

	private static void registerRocks() {

		ROCKS.create(RockType.IGNEOUS, "BlackGranite");
		ROCKS.create(RockType.IGNEOUS, "RedGranite");
		ROCKS.create(RockType.IGNEOUS, "Gabbro");
		ROCKS.create(RockType.METAMORPHIC, "Marble");
		ROCKS.create(RockType.METAMORPHIC, "Eclogite");
		ROCKS.create(RockType.SEDIMENTARY, "Limestone");
		ROCKS.create(RockType.SEDIMENTARY, "Dolomite");

		ROCKS.registerAllRocks();
	}

	private static void registerTiles() {
		Set<String> placeableBlacklist = new HashSet<>();

		Arrays.asList(
			"Opaque",
			"Patches",
			"Web",
			"Threads"
		).forEach(variant -> {
			String fullName = "Grass" + variant;
			String id = "Test:" + fullName;

			register(new TileData(id));
			register(
				new TestTileRenderGrass(
					id,
					getTileTexture(fullName + "Top"),
					getTileTexture(fullName + "Side"),
					variant.equals("Opaque")
				)
			);
			register(new TestTileLogicGrass(id));
		});

		Arrays.asList(
			"Yellow",
			"White",
			"Purple",
			"Blue"
		).forEach(color -> {
			registerSimplestTransparentTile(color + "Flowers");
		});

		registerSimplestTransparentTile("Stones");
		registerSimplestTransparentTile("Sand");

		registerSimplestOpaqueTile("SnowOpaque");
		Arrays.asList(
			"Half",
			"Quarter"
		).forEach(variant -> {
			registerSimplestTransparentTile("Snow" + variant);
		});

		registerSimplestTransparentTile("Clock");
		registerSimplestOpaqueTile("CeilingTile1");
		registerSimplestOpaqueTile("CeilingTile2");
		registerSimplestOpaqueTile("WoodenPlank");
		registerSimplestOpaqueTile("ParquetFloor");
		registerSimplestOpaqueTile("Wallpaper");
		registerSimplestOpaqueTile("WhitePaint");
		registerSimplestOpaqueTile("RoughPaint");
		registerSimplestOpaqueTile("DecorativeBricks");
		registerSimplestTransparentTile("Painting");
		registerSimplestOpaqueTile("TilesLarge");
		registerSimplestOpaqueTile("TilesSmall");

		registerHerb("LowGrass", 6);
		registerHerb("MediumGrass", 6);
		registerHerb("TallGrass", 6);

		Arrays.asList(
			"Dandelion",
			"Lavander"
		).forEach(variant -> {
			String fullName = "Tiny" + variant + "Flowers";
			String id = "Test:" + fullName;

			register(new TileData(id));
			register(new TileRenderTinyFlower(id, getTileTexture(fullName), 8, 0.5f));
			register(new HangingTileLogic(id));
		});

		registerHerb("Bush", 1);
		registerHerb("Fern", 3);
		
		TileDataRegistry.getInstance().values().forEach(PLACEABLE_TILES::add);
		PLACEABLE_TILES.removeIf(b -> placeableBlacklist.contains(b.getId()));
		PLACEABLE_TILES.sort(Comparator.comparing(TileData::getId));
	}

	private static void registerSimplestBlock(String name) {
		String id = "Test:" + name;
		register(new BlockData(id));
		register(new BlockRenderOpaqueCube(id, getBlockTexture(name)));
		register(new BlockLogic(id));
	}

	private static void registerSimplestOpaqueTile(String name) {
		String id = "Test:" + name;
		register(new TileData(id));
		register(new TileRenderOpaqueSurface(id, getTileTexture(name)));
		register(new HangingTileLogic(id));
	}

	private static void registerSimplestTransparentTile(String name) {
		String id = "Test:" + name;
		register(new TileData(id));
		register(new TileRenderTransparentSurface(id, getTileTexture(name)));
		register(new HangingTileLogic(id));
	}

	private static void registerHerb(String name, int maxCount) {
		String id = "Test:" + name;
		register(new TileData(id));
		register(new TileRenderHerb(id, getTileTexture(name), maxCount));
		register(new HangingTileLogic(id));
	}

	private static void registerEntities() {
		float scale = 1.8f / 8;
		registerEntityData("Test:Player", e -> e.setCollisionModel(new AABB(0, 0, 4 * scale, 0.8f, 0.8f, 1.8f)));
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
		triggers.register(
			ControlTriggers.of(
				"Test:BreakBlock",
				KeyEvent.class,
				TestContent::onBlockBreakTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_LEFT).matcher(),
				i -> isAnythingSelected()
			)
		);
		logic.register(ControlLogic.of("Test:BreakBlock", TestContent::onBlockBreakReceived));

		data.register("Test:PlaceBlock", ControlPlaceBlockData::new);
		triggers.register(
			ControlTriggers.of(
				"Test:PlaceBlock",
				KeyEvent.class,
				TestContent::onBlockPlaceTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_RIGHT).matcher(),
				i -> isAnythingSelected() && TestPlayerControls.getInstance().isBlockSelected()
			)
		);

		logic.register(ControlLogic.of("Test:PlaceBlock", TestContent::onBlockPlaceReceived));

		data.register("Test:PlaceTile", ControlPlaceTileData::new);
		triggers.register(
			ControlTriggers.of(
				"Test:PlaceTile",
				KeyEvent.class,
				TestContent::onTilePlaceTrigger,
				KeyMatcher.of(GLFW.GLFW_MOUSE_BUTTON_RIGHT).matcher(),
				i -> isAnythingSelected() && !TestPlayerControls.getInstance().isBlockSelected()
			)
		);
		logic.register(ControlLogic.of("Test:PlaceTile", TestContent::onTilePlaceReceived));

		triggers.register(
			ControlTriggers.localOf(
				"Test:StartNextMusic",
				KeyEvent.class,
				TestMusicPlayer::startNextNow,
				KeyMatcher.of(GLFW.GLFW_KEY_M).matcher()
			)
		);
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
		if (client == null || !client.isReady())
			return null;
		
		return client.getLocalPlayer().getSelection();
	}

	private static boolean isAnythingSelected() {
		ru.windcorp.progressia.client.Client client = ClientState.getInstance();
		if (client == null || !client.isReady())
			return false;

		return client.getLocalPlayer().getSelection().exists();
	}

	private static void onBlockBreakTrigger(ControlData control) {
		((ControlBreakBlockData) control).setBlockInWorld(getSelection().getBlock());
		Sound sfx = new Sound("Progressia:BlockDestroy");
		sfx.setPosition(getSelection().getPoint());
		sfx.setPitch((float) (Math.random() + 1 * 0.5));
		sfx.play(false);
	}

	private static void onBlockBreakReceived(
		Server server,
		PacketControl packet,
		ru.windcorp.progressia.server.comms.Client client
	) {
		Vec3i blockInWorld = ((ControlBreakBlockData) packet.getControl()).getBlockInWorld();
		server.createAbsoluteContext().setBlock(blockInWorld, BlockDataRegistry.getInstance().get("Test:Air"));
	}

	private static void onBlockPlaceTrigger(ControlData control) {
		((ControlPlaceBlockData) control).set(
			TestPlayerControls.getInstance().getSelectedBlock(),
			getSelection().getBlock().add_(getSelection().getSurface().getVector())
		);
	}

	private static void onBlockPlaceReceived(
		Server server,
		PacketControl packet,
		ru.windcorp.progressia.server.comms.Client client
	) {
		ControlPlaceBlockData controlData = ((ControlPlaceBlockData) packet.getControl());
		BlockData block = controlData.getBlock();
		Vec3i blockInWorld = controlData.getBlockInWorld();
		if (server.getWorld().getData().getChunkByBlock(blockInWorld) == null)
			return;
		server.createAbsoluteContext().setBlock(blockInWorld, block);
	}

	private static void onTilePlaceTrigger(ControlData control) {
		((ControlPlaceTileData) control).set(
			TestPlayerControls.getInstance().getSelectedTile(),
			getSelection().getBlock(),
			getSelection().getSurface()
		);
	}

	private static void onTilePlaceReceived(
		Server server,
		PacketControl packet,
		ru.windcorp.progressia.server.comms.Client client
	) {
		ControlPlaceTileData controlData = ((ControlPlaceTileData) packet.getControl());
		TileData tile = controlData.getTile();
		Vec3i blockInWorld = controlData.getBlockInWorld();
		AbsFace face = controlData.getFace();

		if (server.getWorld().getData().getChunkByBlock(blockInWorld) == null) {
			return;
		}
		if (server.getWorld().getData().getTiles(blockInWorld, face).isFull()) {
			return;
		}

		ServerBlockContext context = server.createContext(blockInWorld);
		ServerTileStackContext tsContext = context.push(context.toContext(face));
		ServerTileContext tileContext = tsContext.push(tsContext.getTileCount());

		TileLogic logic = TileLogicRegistry.getInstance().get(tile.getId());
		if (!logic.canOccupyFace(tileContext)) {
			return;
		}
		tileContext.addTile(tile);
	}

	private static void registerMisc() {
		ChunkIO.registerCodec(new TestChunkCodec());

		ChunkRenderOptimizerRegistry.getInstance().register("Core:SurfaceOptimizer", ChunkRenderOptimizerSurface::new);
		ChunkRenderOptimizerRegistry.getInstance().register("Core:SimpleOptimizer", ChunkRenderOptimizerSimple::new);

		GravityModelRegistry.getInstance().register("Test:TheGravityModel", TestGravityModel::new);
		GravityModelRegistry.getInstance().register("Test:PlanetGravityModel", PlanetGravityModel::new);
	}

}
