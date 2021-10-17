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

import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.font.Typeface;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderHorizontal;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;
import ru.windcorp.progressia.client.world.item.inventory.InventoryComponent;

public class InventoryWindow extends Panel {
	
	private static final String CLOSE_CHAR = "\u2715";
	private static final Vec4 CLOSE_BUTTON_IDLE = Colors.toVector(0xFFBC1515);
	private static final Vec4 CLOSE_BUTTON_HOVER = Colors.toVector(0xFFFA6464);
	private static final Vec4 CLOSE_BUTTON_PRESSED = Colors.BLACK;
	
	private final InventoryComponent content;
	private final HUDWorkspace workspace;

	public InventoryWindow(String name, InventoryComponent component, HUDWorkspace workspace) {
		super(name, new LayoutVertical(15, 15));
		this.content = component;
		this.workspace = workspace;

		Group titleBar = new Group(getName() + ".TitleBar", new LayoutBorderHorizontal());
		titleBar.addChild(createLabel(component).setLayoutHint(LayoutBorderHorizontal.CENTER));
		titleBar.addChild(createCloseButton(component).setLayoutHint(LayoutBorderHorizontal.RIGHT));

		addChild(titleBar);

		addChild(component);
	}

	private Label createLabel(InventoryComponent component) {
		String translationKey = "Inventory." + component.getInventory().getId() + ".Title";
		MutableString titleText = new MutableStringLocalized(translationKey);
		Font titleFont = new Font().deriveBold().withColor(Colors.BLACK).withAlign(Typeface.ALIGN_LEFT);

		return new Label(getName() + ".Title", titleFont, titleText);
	}

	private WindowedHUD getManager() {
		Component parent = getParent();
		if (parent instanceof WindowedHUD) {
			return (WindowedHUD) parent;
		}
		return null;
	}

	private Component createCloseButton(InventoryComponent component) {
		
		Button button = new Button(getName() + ".CloseButton", CLOSE_CHAR) {
			
			@Override
			protected void assembleSelf(RenderTarget target) {
				
				Vec4 color = CLOSE_BUTTON_IDLE;
				if (isPressed()) {
					color = CLOSE_BUTTON_PRESSED;
				} else if (isHovered()) {
					color = CLOSE_BUTTON_HOVER;
				}
				
				if (hasLabel()) {
					getLabel().setFont(getLabel().getFont().withColor(color));
				}
				
			}
			
		};
		
		button.addAction(b -> {
			
			content.getInventory().close(workspace.getPlayerEntity());
			
			WindowedHUD manager = getManager();
			if (manager == null) {
				return;
			}
			
			manager.closeWindow(this);
			
		});

		button.setLayout(new LayoutFill());
		
		int height = button.getLabel().getFont().getHeight(button.getLabel().getCurrentText());
		button.setPreferredSize(height, height);
		
		return button;
	}
	
	public InventoryComponent getContent() {
		return content;
	}

}
