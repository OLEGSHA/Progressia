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
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.DynamicLabel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeParts;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.world.item.ItemRenderRegistry;
import ru.windcorp.progressia.client.world.item.ItemRenderable;
import ru.windcorp.progressia.common.world.item.ItemContainer;
import ru.windcorp.progressia.common.world.item.ItemData;
import ru.windcorp.progressia.common.world.item.ItemSlot;

public class SlotComponent extends Component {

	static final float TEXTURE_SIZE = 24;

	private final ItemContainer container;
	private final int index;

	private float scale = 2;

	private ItemRenderable itemRenderer = null;

	private int amountDisplayInt = 0;
	private String amountDisplayString = "";

	private Renderable background = null;

	public SlotComponent(String name, ItemContainer container, int index) {
		super(name);
		this.container = container;
		this.index = index;

		setScale(2);

		Font sizeFont = new Font().deriveOutlined().withScale(1);
		addChild(new DynamicLabel(getName() + ".Size", sizeFont, () -> amountDisplayString, getPreferredSize().x));

		setLayout(new LayoutAlign(0, 0, 0));
	}

	public ItemSlot getSlot() {
		return container.getSlot(index);
	}

	public SlotComponent setScale(float scale) {
		this.scale = scale;

		int side = (int) (TEXTURE_SIZE * scale);
		setPreferredSize(side, side);
		invalidate();

		return this;
	}

	public SlotComponent setBackground(Texture texture) {
		background = new Shape(
			Usage.STATIC,
			FlatRenderProgram.getDefault(),
			ShapeParts.createRectangle(
				FlatRenderProgram.getDefault(),
				texture,
				Colors.WHITE,
				new Vec3(0, 0, 0),
				new Vec3(24, 0, 0),
				new Vec3(0, 24, 0),
				false
			)
		);
		return this;
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		super.assembleSelf(target);
		assembleItem(target);
	}

	private void updateItemRenderer() {
		ItemData contents = getSlot().getContents();

		if (contents == null) {
			itemRenderer = null;
			amountDisplayInt = 0;
			amountDisplayString = "";
		} else {
			if (itemRenderer == null || itemRenderer.getData() != contents) {
				itemRenderer = ItemRenderRegistry.getInstance().get(contents.getId()).createRenderable(contents);
			}

			int newAmount = getSlot().getAmount();
			if (newAmount != amountDisplayInt) {
				amountDisplayInt = newAmount;
				amountDisplayString = newAmount == 1 ? "" : Integer.toString(newAmount);
			}
		}
	}

	private void assembleItem(RenderTarget target) {
		target.pushTransform(new Mat4().translate(getX(), getY(), 0).scale(scale));
		target.addCustomRenderer(renderer -> {

			updateItemRenderer();

			if (itemRenderer != null) {
				itemRenderer.render(renderer);
			} else if (background != null) {
				background.render(renderer);
			}

		});
		target.popTransform();
	}

}
