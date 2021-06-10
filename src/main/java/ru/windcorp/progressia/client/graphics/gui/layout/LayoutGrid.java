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

package ru.windcorp.progressia.client.graphics.gui.layout;

import java.util.Arrays;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Layout;

public class LayoutGrid implements Layout {

	private class GridDimensions {
		int[] columns = new int[4];
		int[] rows = new int[10];
		boolean isSummed = false;

		void add(int column, int row, Vec2i size) {
			if (isSummed)
				throw new IllegalStateException("Already summed");
			columns = update(columns, column, size.x);
			rows = update(rows, row, size.y);
		}

		private int[] update(int[] array, int index, int value) {
			if (array.length <= index) {
				array = Arrays.copyOf(array, ((index / 10) + 1) * 10);
			}

			if (array[index] < value) {
				array[index] = value;
			}

			return array;
		}

		Vec2i getBounds() {
			if (isSummed)
				throw new IllegalStateException("Already summed");
			Vec2i result = new Vec2i(2 * margin - gap, 2 * margin - gap);

			for (int i = 0; i < columns.length; ++i) {
				if (columns[i] != 0) {
					result.x += columns[i] + gap;
				}
			}

			for (int i = 0; i < rows.length; ++i) {
				if (rows[i] != 0) {
					result.y += rows[i] + gap;
				}
			}

			return result;
		}

		void sum() {
			if (isSummed)
				throw new IllegalStateException("Already summed");

			int accumulator = margin;
			int buffer;

			for (int i = 0; i < columns.length; ++i) {
				buffer = columns[i];
				columns[i] = accumulator;
				accumulator += buffer + gap;
			}

			accumulator = margin;

			for (int i = 0; i < rows.length; ++i) {
				buffer = rows[i];
				rows[i] = accumulator;
				accumulator += buffer + gap;
			}

			isSummed = true;
		}

		void setBounds(int column, int row, Component child, Component parent) {
			if (!isSummed)
				throw new IllegalStateException("Not summed yet");

			child.setBounds(parent.getX() + columns[column], parent.getY() + rows[row],

					(column != (columns.length - 1) ? (columns[column + 1] - columns[column] - gap)
							: (parent.getWidth() - margin - columns[column])),

					(row != (rows.length - 1) ? (rows[row + 1] - rows[row] - gap)
							: (parent.getHeight() - margin - rows[row])));
		}
	}

	private int gap, margin;

	public LayoutGrid(int margin, int gap) {
		this.margin = margin;
		this.gap = gap;
	}

	public LayoutGrid(int gap) {
		this(gap, gap);
	}

	public LayoutGrid() {
		this(1);
	}

	@Override
	public void layout(Component c) {
		synchronized (c.getChildren()) {
			GridDimensions grid = calculateGrid(c);
			grid.sum();

			int[] coords;
			for (Component child : c.getChildren()) {
				coords = (int[]) child.getLayoutHint();
				grid.setBounds(coords[0], coords[1], child, c);
			}
		}
	}

	@Override
	public Vec2i calculatePreferredSize(Component c) {
		synchronized (c.getChildren()) {
			return calculateGrid(c).getBounds();
		}
	}

	private GridDimensions calculateGrid(Component parent) {
		GridDimensions result = new GridDimensions();
		int[] coords;

		for (Component child : parent.getChildren()) {
			coords = (int[]) child.getLayoutHint();
			result.add(coords[0], coords[1], child.getPreferredSize());
		}

		return result;
	}

}
