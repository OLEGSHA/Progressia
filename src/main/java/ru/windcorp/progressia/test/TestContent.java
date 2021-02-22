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
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerRegistry;
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
import ru.windcorp.progressia.server.world.entity.*;
import ru.windcorp.progressia.server.world.tile.*;
import ru.windcorp.progressia.test.gen.TestGravityModel;
import ru.windcorp.progressia.test.gen.TestPlanetGravityModel;

public class TestContent {

	public static final String PLAYER_LOGIN = "Sasha";
	public static final long PLAYER_ENTITY_ID = 0x42;
	public static final long STATIE_ENTITY_ID = 0xDEADBEEF;
//	public static final Vec3 SPAWN = new Vec3(8, 8, 880);
	public static final Vec3 SPAWN = new Vec3(0, 0, 66);

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
		register(new BlockRenderOpaqueCube("Test:Dirt", getBlockTexture("Dirt")));
		register(new BlockLogic("Test:Dirt"));

		register(new BlockData("Test:Stone"));
		register(new BlockRenderOpaqueCube("Test:Stone", getBlockTexture("Stone")));
		register(new BlockLogic("Test:Stone"));

		for (String type : new String[] { "Monolith", "Cracked", "Gravel" }) {
			String id = "Test:Granite" + type;

			register(new BlockData(id));
			register(new BlockRenderOpaqueCube(id, getBlockTexture("Granite" + type)));
			register(new BlockLogic(id));
		}

		register(new BlockData("Test:Brick"));
		register(new BlockRenderOpaqueCube("Test:Brick", getBlockTexture("Brick")));
		register(new BlockLogic("Test:Brick"));

		register(new BlockData("Test:BrickWhite"));
		register(new BlockRenderOpaqueCube("Test:BrickWhite", getBlockTexture("BrickWhite")));
		register(new BlockLogic("Test:BrickWhite"));

		register(new BlockData("Test:Glass"));
		register(new BlockRenderTransparentCube("Test:Glass", getBlockTexture("Glass")));
		register(new TestBlockLogicGlass("Test:Glass"));

		register(new BlockData("Test:Sand"));
		register(new BlockRenderOpaqueCube("Test:Sand", getBlockTexture("Sand")));
		register(new BlockLogic("Test:Sand"));

		register(new BlockData("Test:Concrete"));
		register(new BlockRenderOpaqueCube("Test:Concrete", getBlockTexture("ConcreteBlock")));
		register(new BlockLogic("Test:Concrete"));

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

		register(new BlockData("Test:WoodenPlank"));
		register(new BlockRenderOpaqueCube("Test:WoodenPlank", getBlockTexture("WoodenPlank")));
		register(new BlockLogic("Test:WoodenPlank"));

		BlockDataRegistry.getInstance().values().forEach(PLACEABLE_BLOCKS::add);
		PLACEABLE_BLOCKS.removeIf(b -> placeableBlacklist.contains(b.getId()));
		PLACEABLE_BLOCKS.sort(Comparator.comparing(BlockData::getId));

	}

	private static void registerTiles() {
		Set<String> placeableBlacklist = new HashSet<>();

		register(new TileData("Test:Grass"));
		register(new TestTileRenderGrass("Test:Grass", getTileTexture("GrassTop"), getTileTexture("GrassSide")));
		register(new TestTileLogicGrass("Test:Grass"));

		register(new TileData("Test:Stones"));
		register(new TileRenderTransparentSurface("Test:Stones", getTileTexture("Stones")));
		register(new HangingTileLogic("Test:Stones"));

		register(new TileData("Test:YellowFlowers"));
		register(new TileRenderTransparentSurface("Test:YellowFlowers", getTileTexture("YellowFlowers")));
		register(new HangingTileLogic("Test:YellowFlowers"));

		register(new TileData("Test:Sand"));
		register(new TileRenderTransparentSurface("Test:Sand", getTileTexture("Sand")));
		register(new HangingTileLogic("Test:Sand"));

		register(new TileData("Test:SnowOpaque"));
		register(new TileRenderOpaqueSurface("Test:SnowOpaque", getTileTexture("SnowOpaque")));
		register(new HangingTileLogic("Test:SnowOpaque"));

		register(new TileData("Test:SnowHalf"));
		register(new TileRenderTransparentSurface("Test:SnowHalf", getTileTexture("SnowHalf")));
		register(new HangingTileLogic("Test:SnowHalf"));

		register(new TileData("Test:SnowQuarter"));
		register(new TileRenderTransparentSurface("Test:SnowQuarter", getTileTexture("SnowQuarter")));
		register(new HangingTileLogic("Test:SnowQuarter"));

		register(new TileData("Test:Clock"));
		register(new TileRenderTransparentSurface("Test:Clock", getTileTexture("Clock")));
		register(new HangingTileLogic("Test:Clock"));

		register(new TileData("Test:CeilingTile1"));
		register(new TileRenderOpaqueSurface("Test:CeilingTile1", getTileTexture("CeilingTile1")));
		register(new HangingTileLogic("Test:CeilingTile1"));

		register(new TileData("Test:CeilingTile2"));
		register(new TileRenderOpaqueSurface("Test:CeilingTile2", getTileTexture("CeilingTile2")));
		register(new HangingTileLogic("Test:CeilingTile2"));

		register(new TileData("Test:WoodenPlank"));
		register(new TileRenderOpaqueSurface("Test:WoodenPlank", getTileTexture("WoodenPlank")));
		register(new HangingTileLogic("Test:WoodenPlank"));

		register(new TileData("Test:ParquetFloor"));
		register(new TileRenderOpaqueSurface("Test:ParquetFloor", getTileTexture("ParquetFloor")));
		register(new HangingTileLogic("Test:ParquetFloor"));

		register(new TileData("Test:Wallpaper"));
		register(new TileRenderOpaqueSurface("Test:Wallpaper", getTileTexture("Wallpaper")));
		register(new HangingTileLogic("Test:Wallpaper"));

		register(new TileData("Test:WhitePaint"));
		register(new TileRenderOpaqueSurface("Test:WhitePaint", getTileTexture("WhitePaint")));
		register(new HangingTileLogic("Test:WhitePaint"));

		register(new TileData("Test:RoughPaint"));
		register(new TileRenderOpaqueSurface("Test:RoughPaint", getTileTexture("RoughPaint")));
		register(new HangingTileLogic("Test:RoughPaint"));

		register(new TileData("Test:DecorativeBricks"));
		register(new TileRenderOpaqueSurface("Test:DecorativeBricks", getTileTexture("DecorativeBricks")));
		register(new HangingTileLogic("Test:DecorativeBricks"));

		register(new TileData("Test:Painting"));
		register(new TileRenderTransparentSurface("Test:Painting", getTileTexture("Painting")));
		register(new HangingTileLogic("Test:Painting"));

		register(new TileData("Test:TilesLarge"));
		register(new TileRenderOpaqueSurface("Test:TilesLarge", getTileTexture("TilesLarge")));
		register(new HangingTileLogic("Test:TilesLarge"));

		register(new TileData("Test:TilesSmall"));
		register(new TileRenderOpaqueSurface("Test:TilesSmall", getTileTexture("TilesSmall")));
		register(new HangingTileLogic("Test:TilesSmall"));

		TileDataRegistry.getInstance().values().forEach(PLACEABLE_TILES::add);
		PLACEABLE_TILES.removeIf(b -> placeableBlacklist.contains(b.getId()));
		PLACEABLE_TILES.sort(Comparator.comparing(TileData::getId));
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
		SoundEffect sfx = new SoundEffect("Progressia:BlockDestroy");
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
		server.getWorldAccessor().setBlock(blockInWorld, BlockDataRegistry.getInstance().get("Test:Air"));
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
		server.getWorldAccessor().setBlock(blockInWorld, block);
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

		if (server.getWorld().getData().getChunkByBlock(blockInWorld) == null)
			return;
		if (server.getWorld().getData().getTiles(blockInWorld, face).isFull())
			return;
		server.getWorldAccessor().addTile(blockInWorld, face, tile);
	}

	private static void registerMisc() {
		ChunkIO.registerCodec(new TestChunkCodec());
		ChunkRenderOptimizerRegistry.getInstance().register("Core:SurfaceOptimizer", ChunkRenderOptimizerSurface::new);
		GravityModelRegistry.getInstance().register(new TestGravityModel());
		GravityModelRegistry.getInstance().register(new TestPlanetGravityModel());
	}

}
