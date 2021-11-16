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
package ru.windcorp.progressia.client.graphics.world.hud;

import glm.vec._2.Vec2;
import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Layout;

public class WindowedHUD extends Component {
	
	private static class Cookie {
		private final Vec2 relPos = new Vec2();
	}

	public WindowedHUD(String name) {
		super(name);
		setLayout(new Layout() {
			@Override
			public void layout(Component c) {
				for (Component component : c.getChildren()) {
					InventoryWindow window = (InventoryWindow) component;
					
					window.setSize(window.getPreferredSize());
					
					Cookie cookie = (Cookie) window.layoutCookie;
					
					if (cookie == null) {
						window.layoutCookie = cookie = new Cookie();
						cookie.relPos.x = 0.5f;
						cookie.relPos.y = 2 / 3.0f;
					}
					
					cookie.relPos.clamp(0, 1);
					
					window.setPosition(
						(int) (cookie.relPos.x * c.getWidth() - window.getWidth() / 2.0f),
						(int) (cookie.relPos.y * c.getHeight() - window.getHeight())
					);
				}
			}
			
			@Override
			public Vec2i calculatePreferredSize(Component c) {
				throw new AssertionError("welp this wasnt supposed to hapen :(");
			}
		});
	}

	public void addWindow(InventoryWindow window) {
		addChild(window);
		window.setSize(window.getPreferredSize());

		centerWindow(window);
	}
	
	public void closeWindow(InventoryWindow window) {
		removeChild(window);
		requestReassembly();
	}

	private void centerWindow(InventoryWindow window) {
		window.setPosition(
			(getWidth() - window.getWidth()) / 2,
			(getHeight() - window.getHeight()) / 2
		);
	}

}
