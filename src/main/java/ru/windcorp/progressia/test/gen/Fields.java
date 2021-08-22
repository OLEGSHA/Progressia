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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kdotjpg.opensimplex2.areagen.OpenSimplex2S;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.server.world.generation.planet.Planet;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;

public class Fields {

	@FunctionalInterface
	public interface Field {
		double compute(double x, double y);
	}
	
	public static class FieldSet extends Namespaced implements SurfaceFloatField {
		
		private final Field[] fields = new Field[AbsFace.getFaces().size()];
		
		public FieldSet(String id) {
			super(id);
		}
		
		public void put(AbsFace face, Field field) {
			fields[face.getId()] = field;
		}
		
		public Field get(AbsFace face) {
			return fields[face.getId()];
		}

		@Override
		public float get(AbsFace face, float x, float y) {
			float offset = DefaultChunkData.CHUNK_RADIUS - 0.5f;
			return (float) fields[face.getId()].compute(x - offset, y - offset);
		}
		
	}

	private final Random primitiveRandomizer;

	private final OpenSimplex2S noise;

	private final Map<String, FieldSet> registeredFields = Collections.synchronizedMap(new HashMap<>());

	private final Logger logger = LogManager.getLogger(getClass());

	public Fields(long seed) {
		this.primitiveRandomizer = new Random(seed);
		this.noise = new OpenSimplex2S(seed);
	}

	public Field register(String id, AbsFace face, Field f) {
		Objects.requireNonNull(f, "f");
		Objects.requireNonNull(face, "face");

		synchronized (registeredFields) {
			FieldSet fieldSet = registeredFields.computeIfAbsent(id, FieldSet::new);
			
			Field previous = fieldSet.get(face);
			if (previous != null) {
				throw new IllegalArgumentException(
					"Duplicate field definition " + id + ":" + face + " for fields " + f + " and " + previous
				);
			}
			fieldSet.put(face, f);
	
			logger.debug("Registering {}:{} in {}", id, face, getClass().getSimpleName());
		}

		return f;
	}
	
	public SurfaceFloatField register(String id, Function<AbsFace, Field> fieldGenerator) {
		for (AbsFace face : AbsFace.getFaces()) {
			register(id, face, fieldGenerator.apply(face));
		}
		return get(id);
	}
	
	public SurfaceFloatField register(String id, Supplier<Field> fieldGenerator) {
		for (AbsFace face : AbsFace.getFaces()) {
			register(id, face, fieldGenerator.get());
		}
		return get(id);
	}
	
	public SurfaceFloatField get(String id) {
		return registeredFields.get(id);
	}
	
	public Field get(String id, AbsFace face) {
		return registeredFields.get(id).get(face);
	}

	public static Field cutoff(Field f, double outerRadius, double thickness) {
		return (x, y) -> {
			
			double cutoffCoefficient = 1;
			cutoffCoefficient *= cutoffFunction(outerRadius - x, thickness);
			cutoffCoefficient *= cutoffFunction(outerRadius + x, thickness);
			cutoffCoefficient *= cutoffFunction(outerRadius - y, thickness);
			cutoffCoefficient *= cutoffFunction(outerRadius + y, thickness);

			if (cutoffCoefficient == 0) {
				return 0;
			}

			return cutoffCoefficient * f.compute(x, y);
			
		};
	}
	
	public static Field cutoff(Field f, Planet planet, double thickness) {
		return cutoff(f, planet.getRadius() - Coordinates.CHUNK_SIZE, thickness);
	}

	private static double cutoffFunction(double distanceToCutoffPoint, double thickness) {
		if (distanceToCutoffPoint < 0) {
			return 0;
		} else if (distanceToCutoffPoint < thickness) {
			double t = distanceToCutoffPoint / thickness;
			return (1 - Math.cos(Math.PI * t)) / 2;
		} else {
			return 1;
		}
	}

	public Field primitive() {
		double xOffset = primitiveRandomizer.nextDouble() * 200 - 100;
		double yOffset = primitiveRandomizer.nextDouble() * 200 - 100;
		double rotation = primitiveRandomizer.nextDouble() * 2 * Math.PI;

		double sin = Math.sin(rotation);
		double cos = Math.cos(rotation);

		return (x, y) -> noise.noise2(x * cos - y * sin + xOffset, x * sin + y * cos + yOffset);
	}

	public static Field add(Field a, Field b) {
		return (x, y) -> a.compute(x, y) + b.compute(x, y);
	}

	public static Field multiply(Field a, Field b) {
		return (x, y) -> a.compute(x, y) * b.compute(x, y);
	}

	public static Field add(Field... functions) {
		return (x, y) -> {
			double sum = 0;
			for (Field function : functions) {
				sum += function.compute(x, y);
			}
			return sum;
		};
	}

	public static Field multiply(Field... functions) {
		return (x, y) -> {
			double product = 1;
			for (Field function : functions) {
				product *= function.compute(x, y);
			}
			return product;
		};
	}

	public static Field tweak(Field f, double scale, double amplitude, double bias) {
		return (x, y) -> f.compute(x / scale, y / scale) * amplitude + bias;
	}

	public static Field tweak(Field f, double scale, double amplitude) {
		return tweak(f, scale, amplitude, 0);
	}
	
	public static Field scale(Field f, double scale) {
		return tweak(f, scale, 1, 0);
	}
	
	public static Field amplify(Field f, double amplitude) {
		return tweak(f, 1, amplitude, 0);
	}
	
	public static Field bias(Field f, double bias) {
		return tweak(f, 1, 1, bias);
	}
	
	public static Field anti(Field f) {
		return tweak(f, 1, -1, 1);
	}

	public static Field octaves(Field f, double scaleFactor, double amplitudeFactor, int octaves) {
		return (x, y) -> {
			double result = 0;

			double scale = 1;
			double amplitude = 1;
			double cumulativeAmplitude = 0;

			for (int i = 0; i < octaves; ++i) {
				result += f.compute(x * scale, y * scale) * amplitude;
				cumulativeAmplitude += amplitude;
				scale *= scaleFactor;
				amplitude /= amplitudeFactor;
			}

			return result / cumulativeAmplitude;
		};
	}

	public static Field octaves(Field f, double factor, int octaves) {
		return octaves(f, factor, factor, octaves);
	}

	public static Field squash(Field f, double slope) {
		return (x, y) -> 1 / (1 + Math.exp(-slope * f.compute(x, y)));
	}

	public static Field ridge(Field f) {
		return (x, y) -> 1 - Math.abs(f.compute(x, y));
	}

	public static Field clamp(Field f, double min, double max) {
		return (x, y) -> Math.min(Math.max(f.compute(x, y), min), max);
	}
	
	public static Field withMin(Field f, double min) {
		return (x, y) -> Math.max(f.compute(x, y), min);
	}
	
	public static Field withMax(Field f, double max) {
		return (x, y) -> Math.min(f.compute(x, y), max);
	}

	public static Field select(Field f, double target, double width) {
		return (x, y) -> {
			double value = f.compute(x, y);
			if (value < target - width) {
				return 0;
			} else if (value < target) {
				return (width - (target - value)) / width;
			} else if (value < target + width) {
				return (width - (value - target)) / width;
			} else {
				return 0;
			}
		};
	}
	
	public static Field selectPositive(Field f, double target, double width) {
		return (x, y) -> {
			double value = f.compute(x, y);
			if (value < target - width) {
				return 0;
			} else if (value < target + width) {
				return (width - (target - value)) / (2*width);
			} else {
				return 1;
			}
		};
	}
	
	public static Field selectNegative(Field f, double target, double width) {
		return (x, y) -> {
			double value = target - f.compute(x, y);
			if (value < target - width) {
				return 0;
			} else if (value < target + width) {
				return (width - (target - value)) / (2*width);
			} else {
				return 1;
			}
		};
	}

	public static Field cliff(Field f, double target, double featureWidth, double slopeWidth) {
		return (x, y) -> {
			double value = f.compute(x, y);

			if (value < target - featureWidth) {
				return 0;
			} else if (value < target - slopeWidth) {
				double t = (value - (target - featureWidth)) / (featureWidth - slopeWidth);
				return -0.5 + Math.cos(t * Math.PI) / 2;
			} else if (value < target + slopeWidth) {
				double t = (value - (target - slopeWidth)) / (2 * slopeWidth);
				return -Math.cos(t * Math.PI);
			} else if (value < target + featureWidth) {
				double t = (value - (target + slopeWidth)) / (featureWidth - slopeWidth);
				return +0.5 + Math.cos(t * Math.PI) / 2;
			} else {
				return 0;
			}
		};
	}

}
