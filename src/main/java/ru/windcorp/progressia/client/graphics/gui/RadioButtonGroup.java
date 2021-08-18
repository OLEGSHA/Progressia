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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class RadioButtonGroup {

	private final Collection<Consumer<RadioButtonGroup>> actions = Collections
		.synchronizedCollection(new ArrayList<>());
	final List<RadioButton> buttons = Collections.synchronizedList(new ArrayList<>());

	private RadioButton selected = null;

	Consumer<BasicButton> listener = b -> {
		if (b instanceof RadioButton && ((RadioButton) b).isChecked() && buttons.contains(b)) {
			select((RadioButton) b);
		}
	};

	public RadioButtonGroup addAction(Consumer<RadioButtonGroup> action) {
		this.actions.add(Objects.requireNonNull(action, "action"));
		return this;
	}

	public boolean removeAction(Consumer<BasicButton> action) {
		return this.actions.remove(action);
	}

	public List<RadioButton> getButtons() {
		return Collections.unmodifiableList(buttons);
	}

	public synchronized RadioButton getSelected() {
		if (!buttons.contains(selected)) {
			selected = null;
		}
		return selected;
	}

	public synchronized void select(RadioButton button) {
		if (button != null && !buttons.contains(button)) {
			throw new IllegalArgumentException("Button " + button + " is not in the group");
		}

		getSelected(); // Clear if invalid

		if (selected == button) {
			return; // Terminate listener-setter recursion
		}

		if (selected != null) {
			selected.setChecked(false);
		}

		selected = button;

		if (selected != null) {
			selected.setChecked(true);
		}

		actions.forEach(action -> action.accept(this));
	}

	public void selectNext() {
		selectNeighbour(+1);
	}

	public void selectPrevious() {
		selectNeighbour(-1);
	}

	private synchronized void selectNeighbour(int direction) {
		if (getSelected() == null) {
			if (buttons.isEmpty()) {
				throw new IllegalStateException("Cannot select neighbour button: group empty");
			}

			select(buttons.get(0));
		} else {
			RadioButton button;
			int index = buttons.indexOf(selected);

			do {
				index += direction;

				if (index >= buttons.size()) {
					index = 0;
				} else if (index < 0) {
					index = buttons.size() - 1;
				}

				button = buttons.get(index);
			} while (button != getSelected() && !button.isEnabled());

			select(button);
		}
	}

}
