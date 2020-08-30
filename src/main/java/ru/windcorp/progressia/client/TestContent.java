package ru.windcorp.progressia.client;

import static ru.windcorp.progressia.client.world.renders.BlockRenders.register;
import static ru.windcorp.progressia.client.world.renders.BlockRenders.getBlockTexture;
import static ru.windcorp.progressia.client.world.renders.TileRenders.register;
import static ru.windcorp.progressia.client.world.renders.TileRenders.getTileTexture;

import static ru.windcorp.progressia.common.block.BlockDataRegistry.register;
import static ru.windcorp.progressia.common.block.TileDataRegistry.register;

import static ru.windcorp.progressia.server.block.BlockLogicRegistry.register;

import org.lwjgl.glfw.GLFW;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.comms.controls.ControlTriggerOnKeyPress;
import ru.windcorp.progressia.client.comms.controls.ControlTriggerRegistry;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.world.renders.*;
import ru.windcorp.progressia.common.block.*;
import ru.windcorp.progressia.common.comms.controls.ControlData;
import ru.windcorp.progressia.common.comms.controls.ControlDataRegistry;
import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.block.*;
import ru.windcorp.progressia.server.comms.Client;
import ru.windcorp.progressia.server.comms.controls.ControlLogic;
import ru.windcorp.progressia.server.comms.controls.ControlLogicRegistry;

public class TestContent {
	
	public static void registerContent() {
		registerWorldContent();
		regsiterControls();
	}

	private static void registerWorldContent() {
		registerBlocks();
		registerTiles();
	}

	private static void registerBlocks() {
		register(new BlockData("Test", "Air"));
		register(new BlockRenderNone("Test", "Air"));
		register(new BlockLogic("Test", "Air"));

		register(new BlockData("Test", "Dirt"));
		register(new BlockRenderOpaqueCube("Test", "Dirt", getBlockTexture("dirt")));
		register(new BlockLogic("Test", "Dirt"));

		register(new BlockData("Test", "Stone"));
		register(new BlockRenderOpaqueCube("Test", "Stone", getBlockTexture("stone")));
		register(new BlockLogic("Test", "Stone"));

		register(new BlockData("Test", "Compass"));
		register(new BlockRenderOpaqueCube("Test", "Compass", getBlockTexture("compass")));
		register(new BlockLogic("Test", "Compass"));
		
		register(new BlockData("Test", "Glass"));
		register(new BlockRenderTransparentCube("Test", "Glass", getBlockTexture("glass_clear")));
		register(new BlockLogic("Test", "Glass"));
	}

	private static void registerTiles() {
		register(new TileData("Test", "Grass"));
		register(new TileRenderGrass("Test", "Grass", getTileTexture("grass_top"), getTileTexture("grass_side")));
		
		register(new TileData("Test", "Stones"));
		register(new TileRenderSimple("Test", "Stones", getTileTexture("stones")));
		
		register(new TileData("Test", "YellowFlowers"));
		register(new TileRenderSimple("Test", "YellowFlowers", getTileTexture("yellow_flowers")));
		
		register(new TileData("Test", "Sand"));
		register(new TileRenderSimple("Test", "Sand", getTileTexture("sand")));
	}

	private static void regsiterControls() {
		ControlDataRegistry.getInstance().register(new ControlData("Test", "Switch000"));
		ControlTriggerRegistry.getInstance().register(new ControlTriggerOnKeyPress("Test", "Switch000", new KeyMatcher(GLFW.GLFW_KEY_G, new int[0], 0)::matches));
		ControlLogicRegistry.getInstance().register(new ControlLogic("Test", "Switch000") {
			@Override
			public void apply(Server server, PacketControl packet, Client client) {
				Vec3i z000 = new Vec3i(0, 0, 0);
				
				ChunkData data = server.getWorld().getChunk(z000).getData();
				
				BlockData block;
				if (data.getBlock(z000).getId().equals("Test:Stone")) {
					block = BlockDataRegistry.get("Test:Glass");
				} else {
					block =  BlockDataRegistry.get("Test:Stone");
				}
				
				server.getAdHocChanger().setBlock(z000, block);
			}
		});
	}

}
