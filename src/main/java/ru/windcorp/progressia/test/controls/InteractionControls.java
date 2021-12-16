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
package ru.windcorp.progressia.test.controls;

import org.lwjgl.glfw.GLFW;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.audio.Sound;
import ru.windcorp.progressia.client.comms.controls.ControlTriggerRegistry;
import ru.windcorp.progressia.client.comms.controls.ControlTriggers;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.input.WheelScrollEvent;
import ru.windcorp.progressia.client.graphics.world.Selection;
import ru.windcorp.progressia.common.comms.controls.ControlData;
import ru.windcorp.progressia.common.comms.controls.ControlDataRegistry;
import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.controls.ControlLogic;
import ru.windcorp.progressia.server.comms.controls.ControlLogicRegistry;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileStackContext;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicRegistry;
import ru.windcorp.progressia.test.TestContent;

public class InteractionControls {

	private int selectedBlock;
	private int selectedTile;
	private boolean isBlockSelected;
	
	{
		reset();
	}

	public void reset() {
		selectedBlock = 0;
		selectedTile = 0;
		isBlockSelected = true;
	}

	public void registerControls() {
		ControlDataRegistry data = ControlDataRegistry.getInstance();
		ControlTriggerRegistry triggers = ControlTriggerRegistry.getInstance();
		ControlLogicRegistry logic = ControlLogicRegistry.getInstance();

		data.register("Test:BreakBlock", ControlBreakBlockData::new);
		triggers.register(
			ControlTriggers.of(
				"Test:BreakBlock",
				KeyEvent.class,
				this::onBlockBreakTrigger,
				KeyMatcher.LMB::matches,
				i -> isAnythingSelected()
			)
		);
		logic.register(ControlLogic.of("Test:BreakBlock", InteractionControls::onBlockBreakReceived));

		data.register("Test:PlaceBlock", ControlPlaceBlockData::new);
		triggers.register(
			ControlTriggers.of(
				"Test:PlaceBlock",
				KeyEvent.class,
				this::onBlockPlaceTrigger,
				KeyMatcher.RMB::matches,
				i -> isAnythingSelected() && isBlockSelected()
			)
		);

		logic.register(ControlLogic.of("Test:PlaceBlock", InteractionControls::onBlockPlaceReceived));

		data.register("Test:PlaceTile", ControlPlaceTileData::new);
		triggers.register(
			ControlTriggers.of(
				"Test:PlaceTile",
				KeyEvent.class,
				this::onTilePlaceTrigger,
				KeyMatcher.RMB::matches,
				i -> isAnythingSelected() && !isBlockSelected()
			)
		);
		logic.register(ControlLogic.of("Test:PlaceTile", InteractionControls::onTilePlaceReceived));
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:SwitchPlacingModeMMB",
				KeyEvent.class,
				this::switchPlacingMode,
				KeyMatcher.MMB::matches
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:SwitchPlacingModeWheel",
				WheelScrollEvent.class,
				this::switchPlacingMode,
				e -> e.hasHorizontalMovement() || InputTracker.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
			)
		);
		
		triggers.register(
			ControlTriggers.localOf(
				"Test:SelectNextBlockOrTile",
				WheelScrollEvent.class,
				this::selectNextBlockOrTile,
				e -> !e.hasHorizontalMovement() && !InputTracker.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
			)
		);
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

	private void onBlockBreakTrigger(ControlData control) {
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

	private void onBlockPlaceTrigger(ControlData control) {
		((ControlPlaceBlockData) control).set(
			getSelectedBlock(),
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

	private void onTilePlaceTrigger(ControlData control) {
		((ControlPlaceTileData) control).set(
			getSelectedTile(),
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

	public void switchPlacingMode() {
		isBlockSelected = !isBlockSelected;
	}
	
	public void selectNextBlockOrTile(WheelScrollEvent event) {
		if (isBlockSelected) {
			selectedBlock += event.isUp() ? +1 : -1;

			int size = TestContent.PLACEABLE_BLOCKS.size();

			if (selectedBlock < 0) {
				selectedBlock = size - 1;
			} else if (selectedBlock >= size) {
				selectedBlock = 0;
			}
		} else {
			selectedTile += event.isUp() ? +1 : -1;

			int size = TestContent.PLACEABLE_TILES.size();

			if (selectedTile < 0) {
				selectedTile = size - 1;
			} else if (selectedTile >= size) {
				selectedTile = 0;
			}
		}
	}
	
	public BlockData getSelectedBlock() {
		return TestContent.PLACEABLE_BLOCKS.get(selectedBlock);
	}

	public TileData getSelectedTile() {
		return TestContent.PLACEABLE_TILES.get(selectedTile);
	}

	public boolean isBlockSelected() {
		return isBlockSelected;
	}

}
