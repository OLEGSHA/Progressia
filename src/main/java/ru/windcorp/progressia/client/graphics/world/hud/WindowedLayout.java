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

import glm.Glm;
import glm.vec._2.Vec2;
import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Layout;

public class WindowedLayout implements Layout {

	@Override
	public void layout(Component c) {
		for (Component component : c.getChildren()) {
			InventoryWindow window = (InventoryWindow) component;
			
			Vec2i size = new Vec2i(c.getWidth(), c.getHeight());
			Glm.min(window.getPreferredSize(), size, size);
			window.setSize(size);
			
			Vec2 relPos = window.getRelativePosition();
			
			if (Float.isNaN(relPos.x) || Float.isNaN(relPos.y)) {
				relPos.x = 0.5f;
				relPos.y = 2 / 3.0f;
			} else {			
				float minPosX = 0;
				float minPosY = window.getHeight() / (float) c.getHeight();
				float maxPosX = 1;
				float maxPosY = 1;
				
				relPos.x = Glm.clamp(relPos.x, minPosX, maxPosX);
				relPos.y = Glm.clamp(relPos.y, minPosY, maxPosY);
			}
			
			window.setPosition(
				(int) (relPos.x * c.getWidth() - window.getWidth() / 2.0f),
				(int) (relPos.y * c.getHeight() - window.getHeight())
			);
		}
	}

	@Override
	public Vec2i calculatePreferredSize(Component c) {
		throw new UnsupportedOperationException();
	}

}
