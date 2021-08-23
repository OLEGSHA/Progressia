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

package ru.windcorp.progressia.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;

import gnu.trove.TCollections;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TCharFloatHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class Units {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface RegisteredUnit {
		String[] value();
	}

	// Base units
	// We're SI.
	@RegisteredUnit("m")
	public static final float METERS = 1;
	public static final float KILOGRAMS = 1;
	@RegisteredUnit("s")
	public static final float SECONDS = 1;

	// Length
	public static final float CENTIMETERS = METERS / 100;
	public static final float MILLIMETERS = METERS / 1000;
	public static final float KILOMETERS = METERS * 1000;

	// Surface
	public static final float SQUARE_CENTIMETERS = CENTIMETERS * CENTIMETERS;
	public static final float SQUARE_METERS = METERS * METERS;
	public static final float SQUARE_MILLIMETERS = MILLIMETERS * MILLIMETERS;
	public static final float SQUARE_KILOMETERS = KILOMETERS * KILOMETERS;

	// Volume
	public static final float CUBIC_CENTIMETERS = CENTIMETERS * CENTIMETERS * CENTIMETERS;
	public static final float CUBIC_METERS = METERS * METERS * METERS;
	public static final float CUBIC_MILLIMETERS = MILLIMETERS * MILLIMETERS * MILLIMETERS;
	public static final float CUBIC_KILOMETERS = KILOMETERS * KILOMETERS * KILOMETERS;

	// Mass
	@RegisteredUnit("g")
	public static final float GRAMS = KILOGRAMS / 1000;
	@RegisteredUnit("t")
	public static final float TONNES = KILOGRAMS * 1000;

	// Density
	public static final float KILOGRAMS_PER_CUBIC_METER = KILOGRAMS / CUBIC_METERS;
	public static final float GRAMS_PER_CUBIC_CENTIMETER = GRAMS / CUBIC_CENTIMETERS;

	// Time
	public static final float MILLISECONDS = SECONDS / 1000;
	@RegisteredUnit({ "min", "mins", "minute", "minutes" })
	public static final float MINUTES = SECONDS * 60;
	@RegisteredUnit({ "h", "hr", "hrs", "hour", "hours" })
	public static final float HOURS = MINUTES * 60;
	@RegisteredUnit({ "d", "day", "days" })
	public static final float DAYS = HOURS * 24;

	// Frequency
	@RegisteredUnit("Hz")
	public static final float HERTZ = 1 / SECONDS;
	public static final float KILOHERTZ = HERTZ * 1000;

	// Velocity
	public static final float METERS_PER_SECOND = METERS / SECONDS;
	public static final float KILOMETERS_PER_HOUR = KILOMETERS / HOURS;

	// Acceleration
	public static final float METERS_PER_SECOND_SQUARED = METERS_PER_SECOND / SECONDS;

	// Force
	@RegisteredUnit("N")
	public static final float NEWTONS = METERS_PER_SECOND_SQUARED * KILOGRAMS;

	/*
	 * Utilities
	 */

	private static final TObjectFloatMap<String> UNITS_BY_NAME = createMap();

	private static final TCharFloatMap PREFIXES_BY_CHAR;
	static {
		TCharFloatMap prefixes = new TCharFloatHashMap(gnu.trove.impl.Constants.DEFAULT_CAPACITY,
				gnu.trove.impl.Constants.DEFAULT_LOAD_FACTOR, gnu.trove.impl.Constants.DEFAULT_CHAR_NO_ENTRY_VALUE,
				Float.NaN);

		prefixes.put('G', 1e+9f);
		prefixes.put('M', 1e+6f);
		prefixes.put('k', 1e+3f);
		prefixes.put('c', 1e-2f);
		prefixes.put('m', 1e-3f);
		prefixes.put('u', 1e-6f); // not using U+00B5 MICRO SIGN for ease of
									// input

		PREFIXES_BY_CHAR = TCollections.unmodifiableMap(prefixes);
	}

	private static final TObjectFloatMap<String> KNOWN_UNITS = createMap();

	private static TObjectFloatMap<String> createMap() {
		return TCollections.synchronizedMap(new TObjectFloatHashMap<>(gnu.trove.impl.Constants.DEFAULT_CAPACITY,
				gnu.trove.impl.Constants.DEFAULT_LOAD_FACTOR, Float.NaN));
	}

	public static void registerUnits(Class<?> source) throws IllegalAccessException {
		for (Field field : source.getDeclaredFields()) {
			int mods = field.getModifiers();

			if (!Modifier.isPublic(mods))
				continue;
			if (!Modifier.isStatic(mods))
				continue;
			if (!Modifier.isFinal(mods))
				continue;
			if (field.getType() != Float.TYPE)
				continue;

			RegisteredUnit request = field.getAnnotation(RegisteredUnit.class);
			if (request == null)
				continue;

			float value = field.getFloat(null); // adding throws since we might
												// not have accounted for
												// something
			registerUnit(value, request.value());
		}
	}

	public static void registerUnit(float value, String... names) {
		for (String name : names) {
			float previous = UNITS_BY_NAME.put(name, value);

			if (!Float.isNaN(previous)) {
				throw new IllegalArgumentException("Duplicate unit name " + name);
			}
		}

		LogManager.getLogger().debug("Registered unit {} with value {}", Arrays.toString(names), value);
	}

	/**
	 * Returns the value of the unit described by {@code declar}.
	 * <p>
	 * The general form of a declaration is:
	 * 
	 * <pre>
	 * unit_declar       ::= [ws]unit_declar_part[[ws]"/"[ws]unit_declar_part][ws]
	 * unit_declar_part  ::= unit_name_and_exp[[ws]"*"[ws]unit_name_and_exp]+
	 * unit_name_and_exp ::= unit_name[[ws]"^"[ws]exponent]
	 * unit_name         ::= [prefix]named_unit | special_unit
	 * named_unit        ::= &lt;any registered unit name, case-sensitive&gt;
	 * prefix            ::= "G" | "M" | "k" | "c" | "m" | "µ" (\u00B5) | "u"
	 * special_unit      ::= "1"
	 * exponent          ::= &lt;any float&gt;
	 * ws                ::= &lt;any character &lt;= 'U+0020'&gt;+
	 * </pre>
	 * 
	 * Examples:
	 * <ul>
	 * <li>seconds = {@code "s"}</li>
	 * <li>meters per second = {@code "m/s"}</li>
	 * <li>kilonewtons = {@code "kN"}</li>
	 * <li>square meters = {@code "m^2"}</li>
	 * <li>units per meter = {@code "1/m"}</li>
	 * <li>units of gravitational constant G = {@code "m^3/kg*s^2"} [sic] (see
	 * below)</li>
	 * <li>units (dimensionless) = {@code "1"}</li>
	 * </ul>
	 * Note that no more than one {@code '/'} is allowed per declaration, and no
	 * parenthesis are allowed at all. As such,
	 * <ul>
	 * <li>Multiple units under the division bar should be located after the
	 * single {@code '/'} and separated by {@code '*'}: <a
	 * href=https://en.wikipedia.org/wiki/Gas_constant>gas constant</a> ought to
	 * have {@code "J/K*mol"} units.</li>
	 * <li>Exponentiation of parenthesis should be expanded: (m/s)² =
	 * {@code "m^2/s^2"}.</li>
	 * <li>Exponents should also be used for expressing roots: √s =
	 * {@code "s^0.5"}.</li>
	 * <li>Exponents can be used to express division, but such use is generally
	 * discouraged.</li>
	 * </ul>
	 * 
	 * @param unit
	 *            unit declaration
	 * @throws IllegalArgumentException
	 *             if the declaration is invalid
	 * @return the value of the unit
	 * @see #get(String) get(String)
	 * @see #registerUnit(float, String...)
	 */
	public static final float getUnitValue(String unit) {
		float cached = KNOWN_UNITS.get(unit);
		if (!Float.isNaN(cached))
			return cached;

		float computed = computeUnitValue(unit);
		KNOWN_UNITS.put(unit, computed);
		return computed;
	}

	private static float computeUnitValue(String unit) {
		String[] parts = StringUtil.split(unit, '/');

		assert parts != null && parts.length != 0;

		switch (parts.length) {
		case 1:
			return parseUnitValue(parts[0]);
		case 2:
			return parseUnitValue(parts[0]) / parseUnitValue(parts[1]);
		default:
			throw invalidUnit(unit, "unit declaration contains more than one '/'");
		}
	}

	private static float parseUnitValue(String declar) {
		String[] unitsAndExponents = StringUtil.split(declar, '*');

		float result = 1;
		for (String unitAndExponent : unitsAndExponents) {
			String[] parts = StringUtil.split(unitAndExponent, '^');

			float exponent;

			assert parts != null && parts.length != 0;
			switch (parts.length) {
			case 1:
				exponent = 1;
				break;
			case 2:
				exponent = Float.parseFloat(parts[1].trim());
				break;
			default:
				throw invalidUnit(unitAndExponent, "unit declaration contains more than one '^'");
			}

			String unitName = parts[0].trim();

			float value = parseUnitAsNamed(unitName);
			if (Float.isNaN(value))
				value = parseUnitAsNamedAndPrefixed(unitName);
			if (Float.isNaN(value))
				value = parseUnitAsSpecial(unitName);
			if (Float.isNaN(value))
				throw invalidUnit(unitName, "unknown unit name or unknown prefix or unknown special unit");

			if (exponent != 1) {
				value = (float) Math.pow(value, exponent);
			}

			result *= value;
		}
		return result;
	}

	private static float parseUnitAsNamed(String namedUnit) {
		return UNITS_BY_NAME.get(namedUnit);
	}

	private static float parseUnitAsNamedAndPrefixed(String namedUnit) {
		if (namedUnit.length() < 2)
			return Float.NaN;

		float value = PREFIXES_BY_CHAR.get(namedUnit.charAt(0));
		if (!Float.isNaN(value))
			value *= parseUnitAsNamed(namedUnit.substring(1));
		return value;
	}

	private static float parseUnitAsSpecial(String namedUnit) {
		return namedUnit.equals("1") ? 1 : Float.NaN;
	}

	private static RuntimeException invalidUnit(String unit, String details) {
		return new IllegalArgumentException("Invalid unit declaration \"" + unit + "\": " + details);
	}

	public static double get(double amount, String unit) {
		return amount * getUnitValue(unit);
	}

	public static float get(float amount, String unit) {
		return amount * getUnitValue(unit);
	}

	public static float get(String declar) {
		String[] parts = StringUtil.split(declar, ' ', 2);
		assert parts != null && parts.length != 0;
		if (parts[1] == null)
			throw new IllegalArgumentException("No space (' ') found");
		assert parts[0] == parts[0].trim();
		return Float.parseFloat(parts[0]) * getUnitValue(parts[1]);
	}

	public static double getd(String declar) {
		String[] parts = StringUtil.split(declar, ' ', 2);
		assert parts != null && parts.length != 0;
		if (parts[1] == null)
			throw new IllegalArgumentException("No space (' ') found");
		assert parts[0] == parts[0].trim();
		return Double.parseDouble(parts[0]) * getUnitValue(parts[1]);
	}

	static {
		try {
			registerUnits(Units.class);
		} catch (IllegalAccessException e) {
			throw CrashReports.report(e, "Could not register units declared in {}", Units.class.getName());
		}
	}

}
