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
package ru.windcorp.progressia.test.inv;

import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.DynamicLabel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.world.item.ItemRenderRegistry;
import ru.windcorp.progressia.client.world.item.ItemRenderable;
import ru.windcorp.progressia.common.world.item.ItemData;
import ru.windcorp.progressia.common.world.item.ItemSlot;

public class SlotComponent extends Component {

	static final float TEXTURE_SIZE = 24;
	static final float SCALE = 2;

	private final ItemSlot slot;
	private ItemRenderable itemRenderer = null;

	private int sizeDisplayInt = 0;
	private String sizeDisplayString = "";

	public SlotComponent(String name, ItemSlot slot) {
		super(name);
		this.slot = slot;

		int side = (int) (TEXTURE_SIZE * SCALE);
		setPreferredSize(side, side);

		Font sizeFont = new Font().deriveOutlined().withScale(1);
		addChild(new DynamicLabel(name + ".Size", sizeFont, () -> sizeDisplayString, side));

		setLayout(new LayoutAlign(0, 0, 0));
	}

	public ItemSlot getSlot() {
		return slot;
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		super.assembleSelf(target);

		updateItemRenderer();

		assembleItem(target);
	}

	private void updateItemRenderer() {
		ItemData contents = slot.getContents();

		if (contents == null) {
			itemRenderer = null;
			sizeDisplayInt = 0;
			sizeDisplayString = "";
		} else {
			if (itemRenderer == null || itemRenderer.getData() != contents) {
				itemRenderer = ItemRenderRegistry.getInstance().get(contents.getId()).createRenderable(contents);
			}

			int newSize = contents.getSize();
			if (newSize != sizeDisplayInt) {
				sizeDisplayInt = newSize;
				sizeDisplayString = newSize == 1 ? "" : Integer.toString(newSize);
			}
		}
	}

	private void assembleItem(RenderTarget target) {
		if (itemRenderer != null) {
			target.pushTransform(new Mat4().translate(getX(), getY(), 0).scale(SCALE));
			target.addCustomRenderer(itemRenderer);
			target.popTransform();
		}
	}

}
