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

package ru.windcorp.progressia.test.gen;

import kdotjpg.opensimplex2.areagen.OpenSimplex2S;
import ru.windcorp.progressia.server.world.WorldLogic;

class TestTerrainGenerator {

	@FunctionalInterface
	private interface Func2D {
		double compute(double x, double y);
	}

	private final OpenSimplex2S noise;
	private final Func2D shape;

	public TestTerrainGenerator(TestWorldGenerator testWorldGenerator, WorldLogic world) {
		this.noise = new OpenSimplex2S("We're getting somewhere".hashCode());

		Func2D plainsHeight = tweak(octaves(tweak(primitive(), 0.01, 0.5), 2, 3), 1, 0.2, 0.2);

		Func2D mountainsHeight = tweak(octaves(ridge(tweak(primitive(), 0.01, 1)), 2, 1.5, 12), 1, 3);

		Func2D mountainousity = tweak(octaves(tweak(primitive(), 0.007, 1), 2, 3), 1, 1, -0.25);

		shape = tweak(add(multiply(squash(mountainousity, 10), mountainsHeight), plainsHeight), 0.001, 1000, 0);
	}

	public void compute(int startX, int startY, double[][] heightMap, double[][] slopeMap) {
		for (int x = 0; x < heightMap.length; ++x) {
			for (int y = 0; y < heightMap.length; ++y) {
				heightMap[x][y] = shape.compute(x + startX, y + startY);
				slopeMap[x][y] = computeSlope(shape, x + startX, y + startY, heightMap[x][y]);
			}
		}
	}

	private double computeSlope(Func2D f, double x0, double y0, double f0) {
		double di = 0.5;

		double dfdx = (f.compute(x0 + di, y0) - f0) / di;
		double dfdy = (f.compute(x0, y0 + di) - f0) / di;

		return Math.hypot(dfdx, dfdy);
	}

	/*
	 * Utility functions
	 */

	private Func2D primitive() {
		return noise::noise2;
	}

	private Func2D add(Func2D a, Func2D b) {
		return (x, y) -> a.compute(x, y) + b.compute(x, y);
	}

	private Func2D multiply(Func2D a, Func2D b) {
		return (x, y) -> a.compute(x, y) * b.compute(x, y);
	}

	private Func2D tweak(Func2D f, double scale, double amplitude, double bias) {
		return (x, y) -> f.compute(x * scale, y * scale) * amplitude + bias;
	}

	private Func2D tweak(Func2D f, double scale, double amplitude) {
		return tweak(f, scale, amplitude, 0);
	}

	private Func2D octaves(Func2D f, double scaleFactor, double amplitudeFactor, int octaves) {
		return (x, y) -> {
			double result = 0;

			double scale = 1;
			double amplitude = 1;

			for (int i = 0; i < octaves; ++i) {
				result += f.compute(x * scale, y * scale) * amplitude;
				scale *= scaleFactor;
				amplitude /= amplitudeFactor;
			}

			return result;
		};
	}

	private Func2D octaves(Func2D f, double factor, int octaves) {
		return octaves(f, factor, factor, octaves);
	}

	private Func2D squash(Func2D f, double slope) {
		return (x, y) -> 1 / (1 + Math.exp(-slope * f.compute(x, y)));
	}

	private Func2D ridge(Func2D f) {
		return (x, y) -> {
			double result = 1 - Math.abs(f.compute(x, y));
			return result * result;
		};
	}

}
