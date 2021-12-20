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
import static ru.windcorp.progressia.client.world.item.ItemRenderRegistry.getItemTexture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.windcorp.progressia.client.graphics.world.hud.HUDWorkspace;
import ru.windcorp.progressia.client.world.block.*;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerRegistry;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSimple;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSurface;
import ru.windcorp.progressia.client.world.entity.*;
import ru.windcorp.progressia.client.world.item.ItemRender;
import ru.windcorp.progressia.client.world.item.ItemRenderRegistry;
import ru.windcorp.progressia.client.world.item.ItemRenderSimple;
import ru.windcorp.progressia.client.world.item.inventory.InventoryComponent;
import ru.windcorp.progressia.client.world.item.inventory.InventoryComponentSimple;
import ru.windcorp.progressia.client.world.item.inventory.InventoryRender;
import ru.windcorp.progressia.client.world.item.inventory.InventoryRenderRegistry;
import ru.windcorp.progressia.client.world.tile.*;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.state.StatefulObjectRegistry.IdFactory;
import ru.windcorp.progressia.common.world.GravityModelRegistry;
import ru.windcorp.progressia.common.world.block.*;
import ru.windcorp.progressia.common.world.entity.*;
import ru.windcorp.progressia.common.world.io.ChunkIO;
import ru.windcorp.progressia.common.world.item.ItemData;
import ru.windcorp.progressia.common.world.item.ItemDataContainer;
import ru.windcorp.progressia.common.world.item.ItemDataRegistry;
import ru.windcorp.progressia.common.world.item.ItemDataSimple;
import ru.windcorp.progressia.common.world.item.inventory.Inventory;
import ru.windcorp.progressia.common.world.tile.*;
import ru.windcorp.progressia.server.world.block.*;
import ru.windcorp.progressia.server.world.entity.*;
import ru.windcorp.progressia.server.world.generation.planet.PlanetGravityModel;
import ru.windcorp.progressia.server.world.tile.*;
import ru.windcorp.progressia.test.Flowers.FlowerVariant;
import ru.windcorp.progressia.test.Rocks.RockType;
import ru.windcorp.progressia.test.controls.TestPlayerControls;
import ru.windcorp.progressia.test.gen.TestGravityModel;
import ru.windcorp.progressia.test.trees.BlockRenderLeavesHazel;
import ru.windcorp.progressia.test.trees.BlockRenderLeavesPine;

public class TestContent {

	public static final String PLAYER_LOGIN = "Sasha";

	public static final List<BlockData> PLACEABLE_BLOCKS = new ArrayList<>();
	public static final List<TileData> PLACEABLE_TILES = new ArrayList<>();

	public static final Rocks ROCKS = new Rocks();
	public static final Flowers FLOWERS = new Flowers();

	public static void registerContent() {
		registerWorldContent();
		regsiterControls();
		registerMisc();
	}

	private static void registerWorldContent() {
		registerBlocks();
		registerTiles();
		registerItems();
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
		registerSimplestBlock("Mantle");
		registerSimplestBlock("Water");
		registerSimplestBlock("Brick");
		registerSimplestBlock("BrickWhite");
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
		register(new BlockRenderLeavesHazel("Test:TemporaryLeaves", getBlockTexture("LeavesHazel")));
		// Sic, using Glass logic for leaves because Test
		register(new TestBlockLogicGlass("Test:TemporaryLeaves"));

		register(new BlockData("Test:StatieSpawner"));
		register(new BlockRenderOpaqueCube("Test:StatieSpawner", getBlockTexture("StatieSpawner")));
		register(new TestBlockLogicStatieSpawner("Test:StatieSpawner"));
		
		register(new BlockData("Test:Tux"));
		register(new TestBlockRenderTux("Test:Tux"));
		register(new BlockLogic("Test:Tux"));
		
		register(new BlockData("Test:LeavesPine"));
		register(new BlockRenderLeavesPine("Test:LeavesPine", getBlockTexture("LeavesPine")));
		// Sic, using Glass logic for leaves because  Test
		register(new TestBlockLogicGlass("Test:LeavesPine"));

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
		
		registerFlowers();

		registerSimplestTransparentTile("Stones");

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

		registerHerb("GrassMeadow0", 6);
		registerHerb("GrassMeadow1", 6);
		registerHerb("GrassMeadow2", 6);
		registerHerb("GrassBluegrass0", 6);
		registerHerb("GrassBluegrass1", 6);
		registerHerb("GrassBluegrass2", 6);

		registerHerb("Bush", 1);
		registerHerb("Fern", 3);

		TileDataRegistry.getInstance().values().forEach(PLACEABLE_TILES::add);
		PLACEABLE_TILES.removeIf(b -> placeableBlacklist.contains(b.getId()));
		PLACEABLE_TILES.sort(Comparator.comparing(TileData::getId));
	}

	private static void registerFlowers() {
		
		final float common = 0.8f;
		
		FLOWERS.create("Clover", common, FlowerVariant.HERB, FlowerVariant.TINY, FlowerVariant.FLAT);
		FLOWERS.create("Dandelion", common, FlowerVariant.TINY, FlowerVariant.FLAT);
		FLOWERS.create("Geranium", common, FlowerVariant.TINY, FlowerVariant.FLAT);
		FLOWERS.create("Knapweed", common, FlowerVariant.TINY, FlowerVariant.FLAT);
		FLOWERS.create("YellowPea", common, FlowerVariant.TINY, FlowerVariant.FLAT);
		FLOWERS.create("Daisy", common, FlowerVariant.TINY, FlowerVariant.FLAT);
		FLOWERS.create("Lavander", common, FlowerVariant.TINY, FlowerVariant.FLAT);
		
		FLOWERS.registerAllFlowers();
	}
	
	private static void registerItems() {
		registerSimplestItem("MoonTypeIceCream", Units.get("200 g"), Units.get("1 L"));
		registerSimplestItem("Stick", Units.get("260 g"), Units.get("0.5 L"));
		registerSimplestItem("RedGraniteCobblestone", Units.get("4 kg"), Units.get("1500 cm^3"));

		registerItem(
			"Test:CardboardBackpack",
			s -> new ItemDataContainer(
				"Test:CardboardBackpack",
				Units.get("0.7 kg"), // Own mass
				Units.get("5 kg"), // Container mass limit
				Units.get("125 L"), // Own volume
				Units.get("125 L"), // Container volume limit
				false // Whether container contents contribute to item volume
			)
		);
		register(new ItemRenderSimple("Test:CardboardBackpack", getItemTexture("CardboardBackpack")));
		registerSimplestInventory("Test:CardboardBackpack");
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

	private static void registerSimplestItem(String name, float mass, float volume) {
		String id = "Test:" + name;
		registerItem(id, s -> new ItemDataSimple(s, mass, volume));
		register(new ItemRenderSimple(id, getItemTexture(name)));
	}

	private static void registerEntities() {
		registerPlayer();

		registerEntity("Test:Statie", TestEntityDataStatie::new);
		register(new TestEntityRenderStatie("Test:Statie"));
		register(new TestEntityLogicStatie("Test:Statie"));
	}
	
	private static void registerSimplestInventory(String id) {
		InventoryRenderRegistry.getInstance().register(new InventoryRender(id) {
			@Override
			public InventoryComponent createComponent(Inventory inventory, HUDWorkspace workspace) {
				return new InventoryComponentSimple(id, inventory, workspace);
			}
		});
	}

	private static void registerPlayer() {
		SpeciesData human = new SpeciesDataHuman("Core:Human");
		SpeciesDataRegistry.getInstance().register(human);
		SpeciesRenderRegistry.getInstance().register(new SpeciesRenderHuman("Core:Human"));

		registerEntity("Core:Player", id -> new EntityDataPlayer(id, human));
		register(new EntityRenderPlayer("Core:Player"));
		register(new EntityLogic("Core:Player"));
	}

	private static void regsiterControls() {
		TestPlayerControls.getInstance().registerControls();
	}

	private static void register(BlockData x) {
		BlockDataRegistry.getInstance().register(x);
	}

	private static void register(TileData x) {
		TileDataRegistry.getInstance().register(x);
	}

	private static void registerItem(String id, IdFactory<ItemData> factory) {
		ItemDataRegistry.getInstance().register(id, factory);
	}

	private static void registerEntity(
		String id,
		IdFactory<EntityData> factory
	) {
		EntityDataRegistry.getInstance().register(id, factory);
	}

	private static void register(BlockRender x) {
		BlockRenderRegistry.getInstance().register(x);
	}

	private static void register(TileRender x) {
		TileRenderRegistry.getInstance().register(x);
	}

	private static void register(ItemRender x) {
		ItemRenderRegistry.getInstance().register(x);
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

	private static void registerMisc() {
		ChunkIO.registerCodec(new TestChunkCodec());

		ChunkRenderOptimizerRegistry.getInstance().register("Core:SurfaceOptimizer", ChunkRenderOptimizerSurface::new);
		ChunkRenderOptimizerRegistry.getInstance().register("Core:SimpleOptimizer", ChunkRenderOptimizerSimple::new);

		GravityModelRegistry.getInstance().register("Test:TheGravityModel", TestGravityModel::new);
		GravityModelRegistry.getInstance().register("Test:PlanetGravityModel", PlanetGravityModel::new);
	}

}
