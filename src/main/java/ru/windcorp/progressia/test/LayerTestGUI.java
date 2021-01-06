/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.DynamicLabel;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.dynstr.DynamicStrings;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.ServerState;

public class LayerTestGUI extends GUILayer {
	
	public LayerTestGUI() {
		super("LayerTestGui", new LayoutAlign(0, 1, 5));
		
		Panel panel = new Panel("ControlDisplays", new LayoutVertical(5));
		
		Collection<Label> labels = new ArrayList<>();
		Vec4 color = Colors.WHITE;
		Font font = new Font().withColor(color).deriveShadow();
		
		panel.addChild(new Label(
				"IsFlyingDisplay", font,
				() -> String.format("Flying:         %5s (Space bar x2)", TestPlayerControls.getInstance().isFlying())
		));
		
		panel.addChild(new Label(
				"IsMouseCapturedDisplay", font,
				() -> String.format("Mouse captured: %5s (esc)", TestPlayerControls.getInstance().isMouseCaptured())
		));
		
		panel.addChild(new Label(
				"CameraModeDisplay", font,
				() -> String.format("Camera mode:    %5d (F5)", ClientState.getInstance().getCamera().getCurrentModeIndex())
		));
		
		panel.addChild(new Label(
				"GravityModeDisplay", font,
				() -> String.format("Gravity:    %9s (G)", TestPlayerControls.getInstance().useMinecraftGravity() ? "Minecraft" : "Realistic")
		));
		
		panel.addChild(new DynamicLabel(
				"FPSDisplay", font,
				DynamicStrings.builder().add("FPS: ").addDyn(() -> FPS_RECORD.update(GraphicsInterface.getFPS()), 5, 1).buildSupplier(),
				128
		));
		
		panel.addChild(new DynamicLabel(
				"TPSDisplay", font,
				LayerTestGUI::getTPS,
				128
		));
		
		panel.addChild(new DynamicLabel(
				"ChunkUpdatesDisplay", font,
				DynamicStrings.builder().addConst("Pending updates: ").addDyn(ClientState.getInstance().getWorld()::getPendingChunkUpdates).buildSupplier(),
				128
		));
		
		panel.addChild(new DynamicLabel(
				"PosDisplay", font,
				LayerTestGUI::getPos,
				128
		));
		
		panel.addChild(new Label(
				"SelectedBlockDisplay", font,
				() -> String.format(
						"%s Block: %s",
						TestPlayerControls.getInstance().isBlockSelected() ? ">" : " ",
						TestPlayerControls.getInstance().getSelectedBlock().getId()
				)
		));
		panel.addChild(new Label(
				"SelectedTileDisplay", font,
				() -> String.format(
						"%s Tile:  %s",
						TestPlayerControls.getInstance().isBlockSelected() ? " " : ">",
						TestPlayerControls.getInstance().getSelectedTile().getId()
				)
		));
		panel.addChild(new Label(
				"SelectedTileDisplay", font,
				"(Blocks ↔ Tiles: Shift + Mouse Wheel)"
		));
		
		
		panel.getChildren().forEach(c -> {
			if (c instanceof Label) {
				labels.add((Label) c);
			}
		});
		TestPlayerControls.getInstance().setUpdateCallback(() -> labels.forEach(Label::update));

		getRoot().addChild(panel);
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
				if (head == values.length) head = 0;
			} else {
				values[size] = value;
				size++;
			}
		}
		
		public double average() {
			double product = 1;
			
			if (size == values.length) {
				for (double d : values) product *= d;
			} else {
				for (int i = 0; i < size; ++i) product *= values[i];
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
	
	private static final Averager FPS_RECORD = new Averager();
	private static final Averager TPS_RECORD = new Averager();
	
	private static final Supplier<CharSequence> TPS_STRING = DynamicStrings.builder()
			.add("TPS: ")
			.addDyn(() -> TPS_RECORD.update(ServerState.getInstance().getTPS()), 5, 1)
			.buildSupplier();
	
	private static final Supplier<CharSequence> POS_STRING = DynamicStrings.builder()
			.add("Pos: ")
			.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().x, 7, 1)
			.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().y, 7, 1)
			.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().z, 7, 1)
			.buildSupplier();
	
	private static CharSequence getTPS() {
		Server server = ServerState.getInstance();
		if (server == null) return "TPS: n/a";
		
		return TPS_STRING.get();
	}
	
	private static CharSequence getPos() {
		Client client = ClientState.getInstance();
		if (client == null) return "Pos:  client n/a";
		
		Vec3 pos = client.getCamera().getLastAnchorPosition();
		if (Float.isNaN(pos.x)) {
			return "Pos: entity n/a";
		} else {
			return POS_STRING.get();
		}
	}
	
//	private static class DebugComponent extends Component {
//		private final int color;
//		
//		public DebugComponent(String name, Vec2i size, int color) {
//			super(name);
//			this.color = color;
//			
//			setPreferredSize(size);
//			
//			addListener(new Object() {
//				@Subscribe
//				public void onHoverChanged(HoverEvent e) {
//					requestReassembly();
//				}
//			});
//			
//			addListener(KeyEvent.class, this::onClicked);
//		}
//		
//		private boolean onClicked(KeyEvent event) {
//			if (!event.isMouse()) {
//				return false;
//			} else if (event.isPress() && event.isLeftMouseButton()) {
//				System.out.println("You pressed a Component!");
//			}
//			return true;
//		}
//		
//		@Override
//		protected void assembleSelf(RenderTarget target) {
//			target.fill(getX(), getY(), getWidth(), getHeight(), Colors.BLACK);
//			
//			target.fill(
//					getX() + 2, getY() + 2,
//					getWidth() - 4, getHeight() - 4,
//					isHovered() ? Colors.DEBUG_YELLOW : color
//			);
//		}
//	}
//
//	public LayerTestGUI() {
//		super("LayerTestGui", new LayoutAlign(1, 0.75, 5));
//		
//		Panel panel = new Panel("Alex", new LayoutVertical(5));
//		
//		panel.addChild(new DebugComponent("Bravo", new Vec2i(200, 100), 0x44FF44));
//		
//		Component charlie = new DebugComponent("Charlie", null, 0x222222);
//		charlie.setLayout(new LayoutVertical(5));
//
//		//Debug
//		Localizer.getInstance().setLanguage("ru-RU");
//		MutableString epsilon = new MutableStringLocalized("Epsilon")
//				.addListener(() -> ((Label)charlie.getChild(0)).update()).format(34, "thirty-four");
//		// These two are swapped in code due to a bug in layouts, fixing ATM
//		charlie.addChild(
//				new Label(
//						"Delta",
//						new Font().withColor(0xCCBB44).deriveShadow().deriveBold(),
//						"Пре-альфа!"
//				)
//		);
//		charlie.addChild(
//				new Label(
//						"Epsilon",
//						new Font().withColor(0x4444BB).deriveItalic(),
//						() -> epsilon.get().concat("\u269b")
//				)
//		);
//		panel.addChild(charlie);
//
//
//		charlie.addListener(KeyEvent.class, e -> {
//			if(e.isPress() && e.getKey() == GLFW.GLFW_KEY_L) {
//				Localizer localizer = Localizer.getInstance();
//				if (localizer.getLanguage().equals("ru-RU")) {
//					localizer.setLanguage("en-US");
//				} else {
//					localizer.setLanguage("ru-RU");
//				}
//				return true;
//			} return false;
//		});
//		charlie.setFocusable(true);
//		charlie.takeFocus();
//
//		getRoot().addChild(panel);
//	}

}
