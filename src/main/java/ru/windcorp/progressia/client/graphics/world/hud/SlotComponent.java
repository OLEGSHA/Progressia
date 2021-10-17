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

import java.util.function.BooleanSupplier;
import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.DynamicLabel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes.PgmBuilder;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.world.item.ItemRenderRegistry;
import ru.windcorp.progressia.client.world.item.ItemRenderable;
import ru.windcorp.progressia.common.world.item.ItemContainer;
import ru.windcorp.progressia.common.world.item.ItemData;
import ru.windcorp.progressia.common.world.item.ItemDataContainer;
import ru.windcorp.progressia.common.world.item.ItemSlot;

public class SlotComponent extends Component {

	static final float TEXTURE_SIZE = 24;

	private static Renderable containerOpenDecoration = null;
	private static Renderable containerOpenableDecoration = null;

	private final ItemContainer container;
	private final int index;

	private float scale = 2;

	private ItemRenderable itemRenderer = null;

	private int amountDisplayInt = 0;
	private String amountDisplayString = "";

	private Renderable background = null;
	private BooleanSupplier backgroundCondition = null;

	public SlotComponent(String name, ItemContainer container, int index) {
		super(name);
		this.container = container;
		this.index = index;

		setScale(2);

		Font sizeFont = new Font(HUDTextures.getItemAmountTypeface()).deriveOutlined().withScale(2);
		addChild(new DynamicLabel(getName() + ".Size", sizeFont, () -> amountDisplayString, getPreferredSize().x));

		setLayout(new LayoutAlign(0, 0, 0));

		if (containerOpenDecoration == null) {
			containerOpenDecoration = new PgmBuilder(
				FlatRenderProgram.getDefault(),
				HUDTextures.getHUDTexture("DecorationContainerOpen")
			).setSize(TEXTURE_SIZE + 2).setOrigin(-1, -1, 0).create();
		}
		
		if (containerOpenableDecoration == null) {
			containerOpenableDecoration = new PgmBuilder(
				FlatRenderProgram.getDefault(),
				HUDTextures.getHUDTexture("DecorationContainerOpenable")
			).setSize(TEXTURE_SIZE + 2).setOrigin(-1, -1, 0).create();
		}
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

	public SlotComponent setBackground(Texture texture, BooleanSupplier when) {
		background = new PgmBuilder(FlatRenderProgram.getDefault(), texture).setSize(TEXTURE_SIZE).create();
		setBackgroundDisplayCondition(when);
		return this;
	}

	public SlotComponent setBackground(Texture texture) {
		return setBackground(texture, null);
	}

	public SlotComponent setBackgroundDisplayCondition(BooleanSupplier backgroundCondition) {
		this.backgroundCondition = backgroundCondition;
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
		target.pushTransform(new Mat4().translate(getX(), getY(), 0).scale(scale, scale, 1));
		target.addCustomRenderer(renderer -> {

			updateItemRenderer();

			if (itemRenderer != null) {
				itemRenderer.render(renderer);
				renderDecorations(renderer);
			} else if (background != null) {
				if (backgroundCondition == null || backgroundCondition.getAsBoolean()) {
					background.render(renderer);
				}
			}

		});
		target.popTransform();
	}

	private void renderDecorations(ShapeRenderHelper renderer) {
		ItemData contents = getSlot().getContents();

		if (contents instanceof ItemDataContainer) {
			ItemDataContainer asContainer = (ItemDataContainer) contents;
			
			if (asContainer.isOpen()) {
				renderer.pushColorMultiplier().mul(Colors.BLUE);
				containerOpenDecoration.render(renderer);
				renderer.popColorMultiplier();
			} else {
				
				double dx = InputTracker.getCursorX() - (getX() + getWidth() / 2);
				double dy = InputTracker.getCursorY() - (getY() + getHeight() / 2);
				double distanceToCursorSquared = dx*dx + dy*dy;
				final double maxDistanceSquared = (scale * TEXTURE_SIZE * 4) * (scale * TEXTURE_SIZE * 4);
				
				float opacity = (float) (1 - distanceToCursorSquared / maxDistanceSquared);
				
				if (opacity > 0) {
					renderer.pushColorMultiplier().mul(Colors.BLUE.x, Colors.BLUE.y, Colors.BLUE.z, opacity);
					containerOpenableDecoration.render(renderer);
					renderer.popColorMultiplier();
				}
				
			}
		}
	}

}
