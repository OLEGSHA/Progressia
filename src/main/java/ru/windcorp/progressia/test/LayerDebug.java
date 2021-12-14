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

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.DynamicLabel;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;
import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.dynstr.DynamicStrings;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.ServerState;
import ru.windcorp.progressia.test.controls.TestPlayerControls;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class LayerDebug extends GUILayer {
	
	private final List<Runnable> updateTriggers = new ArrayList<>();

	public LayerDebug() {
		super("LayerDebug", new LayoutAlign(0, 1, 5));
		getRoot().addChild(new Group("Displays", new LayoutVertical(5)));

		TestPlayerControls tpc = TestPlayerControls.getInstance();

		addDynamicDisplay(
			"FPSDisplay",
			DynamicStrings.builder()
				.addDyn(new MutableStringLocalized("LayerDebug.FPSDisplay"))
				.addDyn(() -> FPS_RECORD.update(GraphicsInterface.getFPS()), 5, 1)
				.addDyn(() -> GraphicsBackend.isFullscreen() ? " Fullscreen" : "")
				.addDyn(() -> GraphicsBackend.isVSyncEnabled() ? " VSync" : "")
				.buildSupplier()
		);

		addDynamicDisplay("TPSDisplay", LayerDebug::getTPS);

		addDynamicDisplay(
			"ChunkStatsDisplay",
			DynamicStrings.builder()
				.addDyn(new MutableStringLocalized("LayerDebug.ChunkStatsDisplay"))
				.addDyn(() -> {
					if (ClientState.getInstance() == null) {
						return -1;
					} else {
						WorldRender world = ClientState.getInstance().getWorld();
						return world.getChunks().size() - world.getPendingChunkUpdates();
					}
				}, 4)
				.add('/')
				.addDyn(() -> {
					if (ClientState.getInstance() == null) {
						return -1;
					} else {
						return ClientState.getInstance().getWorld().getPendingChunkUpdates();
					}
				}, 4)
				.add('/')
				.addDyn(() -> {
					if (ServerState.getInstance() == null) {
						return -1;
					} else {
						return ServerState.getInstance().getWorld().getChunks().size();
					}
				}, 4)
				.buildSupplier()
		);

		addDynamicDisplay("PosDisplay", LayerDebug::getPos);

		addDisplay("SelectedBlockDisplay", () -> tpc.isBlockSelected() ? ">" : " ", () -> tpc.getSelectedBlock().getId());
		addDisplay("SelectedTileDisplay", () -> tpc.isBlockSelected() ? " " : ">", () -> tpc.getSelectedTile().getId());
		addDisplay("PlacementModeHint", () -> "\u2B04");
	}
	
	private void addDisplay(String name, Supplier<?>... params) {
		Font font = new Font().withColor(Colors.WHITE).deriveOutlined();
		Label component = new Label(name, font, tmp_dynFormat("LayerDebug." + name, params));
		getRoot().getChild(0).addChild(component);
		
		for (Supplier<?> param : params) {
			if (param == null) {
				continue;
			}
			
			updateTriggers.add(new Runnable() {
				
				private Object displayedValue;
				
				@Override
				public void run() {
					Object newValue = param.get();
					if (!Objects.equals(newValue, displayedValue)) {
						component.update();
					}
					displayedValue = newValue;
				}
				
			});
		}
	}
	
	private void addDynamicDisplay(String name, Supplier<CharSequence> contents) {
		Font font = new Font().withColor(Colors.WHITE).deriveOutlined();
		DynamicLabel component = new DynamicLabel(name, font, contents, 128);
		getRoot().getChild(0).addChild(component);
	}
	
	@Override
	protected void doRender() {
		updateTriggers.forEach(Runnable::run);
		super.doRender();
	}

	private static class Averager {

		private static final int DISPLAY_INERTIA = 32;
		private static final double UPDATE_INTERVAL = Units.get(50.0, "ms");

		private final double[] values = new double[DISPLAY_INERTIA];
		private int size;
		private int head;

		private long lastUpdate;

		public void add(double value) {
			if (size == values.length) {
				values[head] = value;
				head++;
				if (head == values.length)
					head = 0;
			} else {
				values[size] = value;
				size++;
			}
		}

		public double average() {
			double product = 1;

			if (size == values.length) {
				for (double d : values)
					product *= d;
			} else {
				for (int i = 0; i < size; ++i)
					product *= values[i];
			}

			return Math.pow(product, 1.0 / size);
		}

		public double update(double value) {
			long now = (long) (GraphicsInterface.getTime() / UPDATE_INTERVAL);
			if (lastUpdate != now) {
				lastUpdate = now;
				add(value);
			}

			return average();
		}

	}

	private static final String[] CLOCK_CHARS = "\u2591\u2598\u259d\u2580\u2596\u258c\u259e\u259b\u2597\u259a\u2590\u259c\u2584\u2599\u259f\u2588"
		.chars().mapToObj(c -> ((char) c) + "").toArray(String[]::new);

	private static String getTPSClockChar() {
		return CLOCK_CHARS[(int) (ServerState.getInstance().getUptimeTicks() % CLOCK_CHARS.length)];
	}

	private static final Averager FPS_RECORD = new Averager();
	private static final Averager TPS_RECORD = new Averager();

	private static final Supplier<CharSequence> TPS_STRING = DynamicStrings.builder()
		.addDyn(new MutableStringLocalized("LayerDebug.TPSDisplay"))
		.addDyn(() -> TPS_RECORD.update(ServerState.getInstance().getTPS()), 5, 1)
		.add(' ')
		.addDyn(LayerDebug::getTPSClockChar)
		.buildSupplier();

	private static final Supplier<CharSequence> POS_STRING = DynamicStrings.builder()
		.addDyn(new MutableStringLocalized("LayerDebug.PosDisplay"))
		.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().x, 7, 1)
		.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().y, 7, 1)
		.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().z, 7, 1)
		.buildSupplier();

	private static CharSequence getTPS() {
		Server server = ServerState.getInstance();
		if (server == null)
			return Localizer.getInstance().getValue("LayerDebug.TPSDisplay.NA");

		return TPS_STRING.get();
	}

	private static CharSequence getPos() {
		Client client = ClientState.getInstance();
		if (client == null)
			return Localizer.getInstance().getValue("LayerDebug.PosDisplay.NA.Client");

		Vec3 pos = client.getCamera().getLastAnchorPosition();
		if (Float.isNaN(pos.x)) {
			return Localizer.getInstance().getValue("LayerDebug.PosDisplay.NA.Entity");
		} else {
			return POS_STRING.get();
		}
	}

	private static MutableString tmp_dynFormat(String formatKey, Supplier<?>... suppliers) {
		return new MutableStringLocalized(formatKey).apply(s -> {
			Object[] args = new Object[suppliers.length];

			for (int i = 0; i < suppliers.length; ++i) {
				Supplier<?> supplier = suppliers[i];

				Object value = supplier != null ? supplier.get() : "null";
				if (!(value instanceof Number)) {
					value = Objects.toString(value);
				}

				args[i] = value;
			}

			return String.format(s, args);
		});
	}

}
