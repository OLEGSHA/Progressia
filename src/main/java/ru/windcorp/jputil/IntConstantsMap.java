/*
 * JPUtil
 * Copyright (C)  2019-2022  OLEGSHA/Javapony and contributors
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

package ru.windcorp.jputil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IntConstantsMap {

	private final Map<Integer, String> namesByValue;
	private final Map<String, Integer> valuesByName;

	protected IntConstantsMap(Map<Integer, String> namesByValue, Map<String, Integer> valuesByName) {
		this.namesByValue = namesByValue;
		this.valuesByName = valuesByName;
	}
	
	public int getValue(String name) {
		Integer value = valuesByName.get(name);
		if (value == null) {
			throw new NoSuchElementException("No constant with name " + name);
		}
		return value.intValue();
	}
	
	public boolean hasConstant(String name) {
		return valuesByName.containsKey(name);
	}
	
	public String getName(int value) {
		String name = namesByValue.get(value);
		if (name == null) {
			throw new NoSuchElementException("No constant with value " + value);
		}
		return name;
	}
	
	public boolean hasConstant(int value) {
		return namesByValue.containsKey(value);
	}
	
	public Map<String, Integer> getAll() {
		return valuesByName;
	}
	
	@Override
	public String toString() {
		return valuesByName.toString();
	}

	public static Builder from(Class<?> clazz) {
		return new Builder(clazz);
	}

	public static class Builder {

		@FunctionalInterface
		public static interface Filter {
			boolean test(String name, int value);
		}
		
		public class ConstantSpec {
			public String name;
			public int value;
			
			public void drop() {
				if (!extra.contains(name)) {
					name = null;
				}
			}
		}

		private final List<Consumer<ConstantSpec>> transforms = new ArrayList<>();
		private final Set<String> extra = new HashSet<>();

		private final Class<?> source;

		public Builder(Class<?> source) {
			this.source = source;
		}
		
		public Builder apply(Consumer<ConstantSpec> transform) {
			transforms.add(transform);
			return this;
		}

		public Builder only(Filter filter) {
			return apply(s -> {
				if (!filter.test(s.name, s.value)) {
					s.drop();
				}
			});
		}

		public Builder only(Predicate<String> nameFilter) {
			return apply(s -> {
				if (!nameFilter.test(s.name)) {
					s.drop();
				}
			});
		}

		public Builder onlyValued(IntPredicate valueFilter) {
			return apply(s -> {
				if (!valueFilter.test(s.value)) {
					s.drop();
				}
			});
		}

		public Builder regex(String regex) {
			return only(Pattern.compile(regex).asPredicate());
		}

		public Builder prefix(String prefix) {
			return only(n -> n.startsWith(prefix) && n.length() > prefix.length());
		}

		public Builder exclude(Filter filter) {
			return only((n, v) -> !filter.test(n, v));
		}

		public Builder exclude(Predicate<String> nameFilter) {
			return only(nameFilter.negate());
		}

		public Builder exclude(String... names) {
			Set<String> excluded = new HashSet<>();
			for (String name : names) {
				excluded.add(name);
			}
			return exclude(excluded::contains);
		}

		public Builder excludeRegex(String... nameRegexes) {
			List<Predicate<String>> tests = new ArrayList<>();
			for (String regex : nameRegexes) {
				tests.add(Pattern.compile(regex).asPredicate());
			}
			return only((n, v) -> {
				for (Predicate<String> test : tests) {
					if (test.test(n)) {
						return false;
					}
				}

				return true;
			});
		}

		public Builder extra(String... names) {
			for (String name : names) {
				extra.add(name);
			}
			return this;
		}
		
		public Builder rename(Function<String, String> renamer) {
			apply(s -> {
				s.name = renamer.apply(s.name);
			});
			return this;
		}
		
		public Builder stripPrefix(String prefix) {
			return apply(s -> {
				if (s.name.startsWith(prefix)) {
					s.name = s.name.substring(prefix.length());
				} else if (extra.contains(s.name)) {
					return;
				} else {
					s.drop();
				}
			});
		}

		public IntConstantsMap scan() {
			return build(true);
		}

		public IntConstantsMap scanAll() {
			return build(false);
		}

		private IntConstantsMap build(boolean onlyPublic) {
			Map<Integer, String> namesByValue = new HashMap<>();
			Map<String, Integer> valuesByName = new HashMap<>();

			BiConsumer<String, Integer> putter = (name, value) -> {
				if (namesByValue.containsKey(value)) {
					throw newDuplicateException("value", value, name, namesByValue.get(value));
				}
				if (valuesByName.containsKey(name)) {
					throw newDuplicateException("name", name, value, valuesByName.get(name));
				}
				namesByValue.put(value, name);
				valuesByName.put(name, value);
			};

			try {
				for (Field field : source.getDeclaredFields()) {
					processField(field, putter, onlyPublic);
				}
			} catch (IllegalAccessException e) {
				throw new ConstantsMapException(e);
			}

			return new IntConstantsMap(
				Collections.unmodifiableMap(namesByValue),
				Collections.unmodifiableMap(valuesByName)
			);
		}

		private void processField(Field field, BiConsumer<String, Integer> putter, boolean onlyPublic)
			throws IllegalAccessException {
			if (!Modifier.isStatic(field.getModifiers())) {
				return;
			}
			if (!Modifier.isFinal(field.getModifiers())) {
				return;
			}

			boolean clearAccessible = false;
			if (!Modifier.isPublic(field.getModifiers())) {
				if (onlyPublic) {
					return;
				} else if (!isAccessibleFlagSet(field)) {
					field.setAccessible(true);
					clearAccessible = true;
				}
			}

			try {

				ConstantSpec spec = new ConstantSpec();
				spec.name = field.getName();
				spec.value = field.getInt(null);
				
				for (Consumer<ConstantSpec> t : transforms) {
					t.accept(spec);
					if (spec.name == null) {
						return;
					}
				}
				
				putter.accept(spec.name, spec.value);

			} finally {
				if (clearAccessible) {
					field.setAccessible(false);
				}
			}
		}
		
		/*
		 * Yes, this method exists only so that neither Java 8 nor Java 9 complain about deprecation.
		 */
		@Deprecated
		private boolean isAccessibleFlagSet(Field f) {
			return f.isAccessible();
		}

		private ConstantsMapException newDuplicateException(String what, Object common, Object current, Object old) {
			return new ConstantsMapException(
				String.format(
					"Duplicate %1$s: %2$s -> %3$s and %2$s -> %4$s",
					what,
					common,
					current,
					old
				)
			);
		}

	}

}
