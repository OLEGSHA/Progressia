/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

import junit.framework.AssertionFailedError;
import ru.windcorp.progressia.common.util.namespaces.IllegalIdException;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.util.namespaces.NamespacedUtil;

public class NamespacedTest {
	
	class TestNamespaced extends Namespaced {

		public TestNamespaced(String id) {
			super(id);
		}
		
	}
	
	void shouldReject(String a, String b) {
		try {
			new TestNamespaced(NamespacedUtil.getId(a, b));
		} catch (IllegalIdException | NullPointerException e) {
			try {
				new TestNamespaced(NamespacedUtil.getId(b, a));
			} catch (IllegalIdException | NullPointerException e1) {
				return;
			}
		}
		
		throw new AssertionFailedError("Expected NPE or IllegalIdException for: \"" + a + "\":\"" + b + "\"");
	}
	
	@Test
	public void shouldAllow() {
		new TestNamespaced("Something:Usual");
		new TestNamespaced("Vry:Sml");
		new TestNamespaced("ALL:CAPS");
		new TestNamespaced("WithDigits12345:MoreDigits67890");
	}
	
	@Test
	public void shouldRejectNulls() {
		shouldReject(null, "Normal");
		shouldReject(null, null);
	}
	
	@Test
	public void shouldRejectInvalid() {
		shouldReject("Contains-hyphens", "Normal");
		shouldReject("Contains_underscores", "Normal");
		shouldReject("ALL_CAPS_WITH_UNDERSCORES", "Normal");
		shouldReject("Contains whitespace", "Normal");
		shouldReject("0StartsWithDigit", "Normal");
		shouldReject("lowerCamelCase", "Normal");
		shouldReject("XS", "Normal");
		shouldReject("", "Normal");
		shouldReject("Contains:separators", "Normal");
		shouldReject("СодержитНеАльфанум", "Normal");
	}
	
	@Test
	public void shouldRejectGarbage() {
		Random random = new Random(0);

		byte[] bytes = new byte[NamespacedUtil.MAX_NAME_LENGTH];
		for (int attempt = 0; attempt < 10000; ++attempt) {
			random.nextBytes(bytes);
			bytes[bytes.length - 1] = '!'; // Make sure it is invalid
			shouldReject(new String(bytes), "ContainsUtterGarbage");
		}
	}
	
	@Test
	public void testHashCodeAndEquals() {
		HashSet<TestNamespaced> hashSet = new HashSet<>();
		
		Collection<TestNamespaced> contains = new ArrayList<>();
		Collection<TestNamespaced> doesNotContain = new ArrayList<>();
		
		Random random = new Random(0);
		
		for (int i = 0; i < 256; ++i) {
			String namespace = getRandomValidString(random);
			String name = getRandomValidString(random);
			
			TestNamespaced a = new TestNamespaced(NamespacedUtil.getId(namespace, name));
			TestNamespaced b = new TestNamespaced(NamespacedUtil.getId(namespace, name));
			
			contains.add(a);
			hashSet.add(b);
		}
		
		for (int i = 0; i < 256; ++i) {
			String namespace = getRandomValidString(random);
			String name = getRandomValidString(random);
			
			TestNamespaced c = new TestNamespaced(NamespacedUtil.getId(namespace, name));
			
			doesNotContain.add(c);
		}
		
		for (TestNamespaced x : contains) {
			Iterator<TestNamespaced> it = doesNotContain.iterator();
			while (it.hasNext()) {
				TestNamespaced next = it.next();
				if (next.getId().equals(x.getId())) {
					it.remove();
				}
			}
		}
		
		for (TestNamespaced test : contains) {
			assertTrue(hashSet.contains(test));
		}
		
		for (TestNamespaced test : doesNotContain) {
			assertFalse(hashSet.contains(test));
		}
	}
	
	String getRandomValidString(Random random) {
		char[] chars = new char[random.nextInt(NamespacedUtil.MAX_NAME_LENGTH - 3) + 3];
		
		for (int i = 0; i < chars.length; ++i) {
			switch (random.nextInt(3)) {
			case 0:
				if (i != 0) {
					chars[i] = (char) ('a' + random.nextInt('z' - 'a'));
					break;
				}
			case 1:
				if (i != 0) {
					chars[i] = (char) ('0' + random.nextInt('9' - '0'));
					break;
				}
			case 2:
				chars[i] = (char) ('A' + random.nextInt('Z' - 'A'));
				break;
			}
		}
		
		return new String(chars);
	}

}
