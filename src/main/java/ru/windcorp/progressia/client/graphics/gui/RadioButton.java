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

import org.lwjgl.glfw.GLFW;

import glm.vec._2.i.Vec2i;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.font.Typefaces;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutHorizontal;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;

public class RadioButton extends BasicButton {

	private class Tick extends Component {

		public Tick() {
			super(RadioButton.this.getName() + ".Tick");

			setPreferredSize(new Vec2i(Typefaces.getDefault().getLineHeight() * 3 / 2));
		}

		private void cross(RenderTarget target, int x, int y, int size, Vec4 color) {
			target.fill(x + 4, y, size - 8, size, color);
			target.fill(x + 2, y + 2, size - 4, size - 4, color);
			target.fill(x, y + 4, size, size - 8, color);
		}

		@Override
		protected void assembleSelf(RenderTarget target) {

			int size = getPreferredSize().x;
			int x = getX();
			int y = getY() + (getHeight() - size) / 2;

			// Border

			Vec4 borderColor;
			if (RadioButton.this.isPressed() || RadioButton.this.isHovered() || RadioButton.this.isFocused()) {
				borderColor = Colors.BLUE;
			} else {
				borderColor = Colors.LIGHT_GRAY;
			}
			cross(target, x, y, size, borderColor);

			// Inside area

			if (RadioButton.this.isPressed()) {
				// Do nothing
			} else {
				Vec4 backgroundColor;
				if (RadioButton.this.isHovered() && RadioButton.this.isEnabled()) {
					backgroundColor = Colors.HOVER_BLUE;
				} else {
					backgroundColor = Colors.WHITE;
				}
				cross(target, x + 2, y + 2, size - 4, backgroundColor);
			}

			// "Tick"

			if (RadioButton.this.isChecked()) {
				cross(target, x + 4, y + 4, size - 8, Colors.BLUE);
			}
		}

	}

	private boolean checked;

	private RadioButtonGroup group = null;

	public RadioButton(String name, String label, Font labelFont, boolean check) {
		super(name, label, labelFont);
		this.checked = check;

		assert getChildren().size() == 1 : "RadioButton expects that BasicButton contains exactly one child";
		Component basicChild = getChild(0);

		Group group = new Group(getName() + ".LabelAndTick", new LayoutHorizontal(0, 10));
		removeChild(basicChild);
		setLayout(new LayoutAlign(0, 0.5f, 10));
		group.setLayoutHint(basicChild.getLayoutHint());
		group.addChild(new Tick());
		group.addChild(basicChild);
		addChild(group);

		addInputListener(KeyEvent.class, e -> {
			if (e.isRelease()) return;

			if (e.getKey() == GLFW.GLFW_KEY_LEFT || e.getKey() == GLFW.GLFW_KEY_UP) {
				if (this.group != null) {
					this.group.selectPrevious();
					this.group.getSelected().takeFocus();
				}
				e.consume();
			} else if (e.getKey() == GLFW.GLFW_KEY_RIGHT || e.getKey() == GLFW.GLFW_KEY_DOWN) {
				if (this.group != null) {
					this.group.selectNext();
					this.group.getSelected().takeFocus();
				}
				e.consume();
			}
		});

		addAction(b -> setChecked(true));
	}

	public RadioButton(String name, String label, Font labelFont) {
		this(name, label, labelFont, false);
	}

	public RadioButton(String name, String label, boolean check) {
		this(name, label, new Font(), check);
	}

	public RadioButton(String name, String label) {
		this(name, label, false);
	}

	/**
	 * @param group the group to set
	 */
	public RadioButton setGroup(RadioButtonGroup group) {

		if (this.group != null) {
			group.selectNext();
			removeAction(group.listener);
			group.buttons.remove(this);
			group.getSelected(); // Clear reference if this was the only button
									// in the group
		}

		this.group = group;

		if (this.group != null) {
			group.buttons.add(this);
			addAction(group.listener);
		}

		setChecked(false);

		return this;
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

		if (group != null) {
			group.listener.accept(this); // Failsafe for manual invocations of
											// setChecked()
		}
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
