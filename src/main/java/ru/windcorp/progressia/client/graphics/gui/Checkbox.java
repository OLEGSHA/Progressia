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
package ru.windcorp.progressia.client.graphics.gui;

import glm.vec._2.i.Vec2i;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.font.Typefaces;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutHorizontal;

public class Checkbox extends BasicButton {
	
	private class Tick extends Component {

		public Tick() {
			super(Checkbox.this.getName() + ".Tick");
			
			setPreferredSize(new Vec2i(Typefaces.getDefault().getLineHeight() * 3 / 2));
		}
		
		@Override
		protected void assembleSelf(RenderTarget target) {
			
			int size = getPreferredSize().x;
			int x = getX();
			int y = getY() + (getHeight() - size) / 2;
			
			// Border
			
			Vec4 borderColor;
			if (Checkbox.this.isPressed() || Checkbox.this.isHovered() || Checkbox.this.isFocused()) {
				borderColor = Colors.BLUE;
			} else {
				borderColor = Colors.LIGHT_GRAY;
			}
			target.fill(x, y, size, size, borderColor);
			
			// Inside area
			
			if (Checkbox.this.isPressed()) {
				// Do nothing
			} else {
				Vec4 backgroundColor;
				if (Checkbox.this.isHovered() && Checkbox.this.isEnabled()) {
					backgroundColor = Colors.HOVER_BLUE;
				} else {
					backgroundColor = Colors.WHITE;
				}
				target.fill(x + 2, y + 2, size - 4, size - 4, backgroundColor);
			}
			
			// "Tick"
			
			if (Checkbox.this.isChecked()) {
				target.fill(x + 4, y + 4, size - 8, size - 8, Colors.BLUE);
			}
		}

	}

	private boolean checked;

	public Checkbox(String name, String label, Font labelFont, boolean check) {
		super(name, label, labelFont);
		this.checked = check;
		
		assert getChildren().size() == 1 : "Checkbox expects that BasicButton contains exactly one child";
		Component basicChild = getChild(0);
		
		Group group = new Group(getName() + ".LabelAndTick", new LayoutHorizontal(0, 10));
		removeChild(basicChild);
		setLayout(new LayoutAlign(0, 0.5f, 10));
		group.setLayoutHint(basicChild.getLayoutHint());
		group.addChild(new Tick());
		group.addChild(basicChild);
		addChild(group);
		
		addAction(b -> switchState());
	}
	
	public Checkbox(String name, String label, Font labelFont) {
		this(name, label, labelFont, false);
	}
	
	public Checkbox(String name, String label, boolean check) {
		this(name, label, new Font(), check);
	}
	
	public Checkbox(String name, String label) {
		this(name, label, false);
	}

	public void switchState() {
		setChecked(!isChecked());
	}
	
	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}
	
	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		// Change label font color
		
		if (isPressed()) {
			getLabel().setFont(getLabel().getFont().withColor(Colors.BLUE));
		} else {
			getLabel().setFont(getLabel().getFont().withColor(Colors.BLACK));
		}
	}
	
	@Override
	protected void postAssembleSelf(RenderTarget target) {
		// Apply disable tint
		
		if (!isEnabled()) {
			target.fill(getX(), getY(), getWidth(), getHeight(), Colors.toVector(0x88FFFFFF));
		}
	}
	
}
