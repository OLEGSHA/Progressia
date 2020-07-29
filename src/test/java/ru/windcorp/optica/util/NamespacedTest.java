package ru.windcorp.optica.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

import junit.framework.AssertionFailedError;
import ru.windcorp.optica.common.util.Namespaced;

public class NamespacedTest {
	
	class TestNamespaced extends Namespaced {

		public TestNamespaced(String namespace, String name) {
			super(namespace, name);
		}
		
	}
	
	void shouldReject(String a, String b) {
		try {
			new TestNamespaced(a, b);
		} catch (IllegalArgumentException | NullPointerException e) {
			try {
				new TestNamespaced(b, a);
			} catch (IllegalArgumentException | NullPointerException e1) {
				return;
			}
		}
		
		throw new AssertionFailedError("Expected NPE or IAE for: \"" + a + "\":\"" + b + "\"");
	}
	
	@Test
	public void shouldAllow() {
		new TestNamespaced("Something", "Usual");
		new TestNamespaced("Vry", "Sml");
		new TestNamespaced("ALL", "CAPS");
		new TestNamespaced("WithDigits12345", "MoreDigits67890");
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
		shouldReject("СодержитРусский", "Normal");
	}
	
	@Test
	public void shouldRejectGarbage() {
		Random random = new Random(0);

		byte[] bytes = new byte[1024];
		for (int attempt = 0; attempt < 10000; ++attempt) {
			random.nextBytes(bytes);
			bytes[0] = 'a'; // Make sure it is invalid
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
			
			TestNamespaced a = new TestNamespaced(namespace, name);
			TestNamespaced b = new TestNamespaced(namespace, name);
			
			contains.add(a);
			hashSet.add(b);
		}
		
		for (int i = 0; i < 256; ++i) {
			String namespace = getRandomValidString(random);
			String name = getRandomValidString(random);
			
			TestNamespaced c = new TestNamespaced(namespace, name);
			
			doesNotContain.add(c);
		}
		
		for (TestNamespaced x : contains) {
			Iterator<TestNamespaced> it = doesNotContain.iterator();
			while (it.hasNext()) {
				TestNamespaced next = it.next();
				if (next.getName().equals(x.getName()) && next.getNamespace().equals(x.getNamespace())) {
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
		char[] chars = new char[random.nextInt(100) + 3];
		
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
