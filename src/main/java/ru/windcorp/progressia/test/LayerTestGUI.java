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

import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;

public class LayerTestGUI extends GUILayer {
	
	public LayerTestGUI() {
		super("LayerTestGui", new LayoutAlign(0, 1, 5));
		
		Panel panel = new Panel("ControlDisplays", new LayoutVertical(5));
		
		Collection<Label> labels = new ArrayList<>();
		
		panel.addChild(new Label(
				"IsFlyingDisplay", new Font().withColor(0x37A3E6).deriveShadow(),
				() -> String.format("Flying:         %5s (Space bar x2)", TestPlayerControls.getInstance().isFlying())
		));
		
		panel.addChild(new Label(
				"IsMouseCapturedDisplay", new Font().withColor(0x37A3E6).deriveShadow(),
				() -> String.format("Mouse captured: %5s (esc)", TestPlayerControls.getInstance().isMouseCaptured())
		));
		
		panel.addChild(new Label(
				"CameraModeDisplay", new Font().withColor(0x37A3E6).deriveShadow(),
				() -> String.format("Camera mode:    %5d (F5)", ClientState.getInstance().getCamera().getCurrentModeIndex())
		));
		
		panel.addChild(new Label(
				"GravityModeDisplay", new Font().withColor(0x37A3E6).deriveShadow(),
				() -> String.format("Gravity:    %9s (G)", TestPlayerControls.getInstance().useMinecraftGravity() ? "Minecraft" : "Realistic")
		));
		
		panel.getChildren().forEach(c -> labels.add((Label) c));
		TestPlayerControls.getInstance().setUpdateCallback(() -> labels.forEach(Label::update));

		getRoot().addChild(panel);
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