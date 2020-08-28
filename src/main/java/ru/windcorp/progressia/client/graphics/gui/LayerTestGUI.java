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
package ru.windcorp.progressia.client.graphics.gui;

import com.google.common.eventbus.Subscribe;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.event.HoverEvent;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.localization.Localizer;

import java.net.http.WebSocket;

public class LayerTestGUI extends GUILayer {
	
	private static class DebugComponent extends Component {
		private final int color;
		
		public DebugComponent(String name, Vec2i size, int color) {
			super(name);
			this.color = color;
			
			setPreferredSize(size);
			
			addListener(new Object() {
				@Subscribe
				public void onHoverChanged(HoverEvent e) {
					requestReassembly();
				}
			});
			
			addListener(KeyEvent.class, this::onClicked);
		}
		
		private boolean onClicked(KeyEvent event) {
			if (event.isPress() && event.isLeftMouseButton()) {
				System.out.println("You pressed a Component!");
			}
			return true;
		}
		
		@Override
		protected void assembleSelf(RenderTarget target) {
			target.fill(getX(), getY(), getWidth(), getHeight(), Colors.BLACK);
			
			target.fill(
					getX() + 2, getY() + 2,
					getWidth() - 4, getHeight() - 4,
					isHovered() ? Colors.DEBUG_YELLOW : color
			);
		}
	}

	public LayerTestGUI() {
		super("LayerTestGui", new LayoutAlign(1, 0.75, 5));
		
		Panel panel = new Panel("Alex", new LayoutVertical(5));
		
		panel.addChild(new DebugComponent("Bravo", new Vec2i(200, 100), 0x44FF44));
		
		Component charlie = new DebugComponent("Charlie", null, 0x222222);
		charlie.setLayout(new LayoutVertical());

		//Debug
		Localizer.getInstance().setLanguage("ru-RU");
		// These two are swapped in code due to a bug in layouts, fixing ATM
		charlie.addChild(
				new Label(
						"Epsilon",
						new Font().withColor(0x4444BB).deriveItalic(),
						Localizer.getInstance().getValue("Epsilon")+"\u269b"
				)
		);
		charlie.addChild(
				new Label(
						"Delta",
						new Font().withColor(0xCCBB44).deriveShadow().deriveBold(),
						"Пре-альфа!"
				)
		);
		panel.addChild(charlie);
		
		getRoot().addChild(panel);
	}

}
