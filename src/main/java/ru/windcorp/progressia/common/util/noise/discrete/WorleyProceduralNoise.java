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
package ru.windcorp.progressia.common.util.noise.discrete;

import java.util.Collection;
import com.google.common.collect.ImmutableList;

public class WorleyProceduralNoise<T> implements DiscreteNoise<T> {

	/*
	 * Stolen from OpenJDK's Random implementation
	 * *evil cackling*
	 */
	private static final long MULTIPLIER = 0x5DEECE66DL;
	private static final long ADDEND = 0xBL;
	private static final long MASK = (1L << 48) - 1;
	private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)

	private static long permute(long seed) {
		return (seed * MULTIPLIER + ADDEND) & MASK;
	}

	private static double getDouble(long seed) {
		final int mask26bits = (1 << 26) - 1;
		final int mask27bits = (1 << 27) - 1;

		int randomBitsX26 = (int) (seed & 0xFFFFFFFF);
		int randomBitsX27 = (int) ((seed >>> Integer.SIZE) & 0xFFFFFFFF);

		randomBitsX26 = randomBitsX26 & mask26bits;
		randomBitsX27 = randomBitsX27 & mask27bits;

		return (((long) (randomBitsX26) << 27) + randomBitsX27) * DOUBLE_UNIT;
	}

	public static class Entry<T> {
		private final T value;
		private final double chance;

		public Entry(T value, double chance) {
			this.value = value;
			this.chance = chance;
		}
	}

	public static class Builder<T> {

		com.google.common.collect.ImmutableList.Builder<Entry<T>> builder = ImmutableList.builder();

		public Builder<T> add(T value, double chance) {
			builder.add(new Entry<>(value, chance));
			return this;
		}

		public WorleyProceduralNoise<T> build(long seed) {
			return new WorleyProceduralNoise<>(this, seed);
		}

	}

	public static <T> Builder<T> builder() {
		return new Builder<>();
	}

	private final Entry<?>[] entries;
	private final long seed;

	public WorleyProceduralNoise(Builder<T> builder, long seed) {
		this(builder.builder.build(), seed);
	}

	public WorleyProceduralNoise(Collection<? extends Entry<? extends T>> entries, long seed) {
		this.entries = new Entry<?>[entries.size()];

		double chancesSum = 0;
		for (Entry<? extends T> entry : entries) {
			chancesSum += entry.chance;
		}

		int i = 0;
		for (Entry<? extends T> entry : entries) {
			this.entries[i] = new Entry<T>(entry.value, entry.chance / chancesSum);
			i++;
		}

		this.seed = seed;
	}

	@Override
	public T get(double x, double y) {

		int ox = (int) x;
		int oy = (int) y;

		T closest = null;
		double closestDistanceSq = Double.POSITIVE_INFINITY;

		for (int cellX = ox - 1; cellX <= ox + 1; ++cellX) {
			for (int cellY = oy - 1; cellY <= oy + 1; ++cellY) {

				long cellSeed = permute(cellY ^ permute(cellX ^ seed));

				int nodes = getNodeCount(cellSeed);
				cellSeed = permute(cellSeed);

				for (int i = 0; i < nodes; ++i) {

					double nodeX = getDouble(cellSeed) + cellX;
					cellSeed = permute(cellSeed);

					double nodeY = getDouble(cellSeed) + cellY;
					cellSeed = permute(cellSeed);

					T value = getValue(getDouble(cellSeed));
					cellSeed = permute(cellSeed);

					double distanceSq = (x - nodeX) * (x - nodeX) + (y - nodeY) * (y - nodeY);
					if (distanceSq < closestDistanceSq) {
						closestDistanceSq = distanceSq;
						closest = value;
					}

				}
			}
		}

		return closest;

	}

	@Override
	public T get(double x, double y, double z) {

		int ox = (int) x;
		int oy = (int) y;
		int oz = (int) z;

		T closest = null;
		double closestDistanceSq = Double.POSITIVE_INFINITY;

		for (int cellX = ox - 1; cellX <= ox + 1; ++cellX) {
			for (int cellY = oy - 1; cellY <= oy + 1; ++cellY) {
				for (int cellZ = oz - 1; cellZ <= oz + 1; ++cellZ) {

					long cellSeed = permute(cellZ ^ permute(cellY ^ permute(cellX ^ seed)));

					int nodes = getNodeCount(cellSeed);
					cellSeed = permute(cellSeed);

					for (int i = 0; i < nodes; ++i) {

						double nodeX = getDouble(cellSeed) + cellX;
						cellSeed = permute(cellSeed);

						double nodeY = getDouble(cellSeed) + cellY;
						cellSeed = permute(cellSeed);

						double nodeZ = getDouble(cellSeed) + cellZ;
						cellSeed = permute(cellSeed);

						T value = getValue(getDouble(cellSeed));
						cellSeed = permute(cellSeed);

						double distanceSq = (x - nodeX) * (x - nodeX) + (y - nodeY) * (y - nodeY)
							+ (z - nodeZ) * (z - nodeZ);
						if (distanceSq < closestDistanceSq) {
							closestDistanceSq = distanceSq;
							closest = value;
						}

					}
				}
			}
		}

		return closest;

	}

	@SuppressWarnings("unchecked")
	private T getValue(double target) {
		int i;

		for (i = 0; i < entries.length && target > entries[i].chance; ++i) {
			target -= entries[i].chance;
		}

		return (T) entries[i].value;
	}

	private int getNodeCount(long seed) {
		int uniform = ((int) seed) % 8;

		switch (uniform) {
		case 0:
		case 1:
		case 2:
		case 3:
			return 1;

		case 4:
		case 5:
			return 2;

		case 6:
			return 3;

		default:
			return 4;
		}
	}

}
