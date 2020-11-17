package ru.windcorp.jputil.chars.stringUtil;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import ru.windcorp.jputil.chars.StringUtil;

public class SplitAtTest {
	
	@Test
	public void testExamplesFromDocs() {
		test("a.b.c", new int[] {1, 3},    new String[] {"a", "b", "c"});
		test("a..b",  new int[] {1, 2},    new String[] {"a", "", "b"});
		test(".b.",   new int[] {0, 2},    new String[] {"", "b", ""});
		test("a.b",   new int[] {1, 1, 1}, new String[] {"a", "", "", "b"});
	}
	
	@Test
	public void testIndexPermutations() {
		Random random = new Random(0);
		
		int stringLength = 1000;
		char[] chars = new char[stringLength];
		
		for (int i = 0; i < stringLength; ++i) {
			chars[i] = (char) ('a' + random.nextInt('z' - 'a'));
		}
		
		String src = new String(chars);
		
		int[] indices = new int[100];
		
		for (int i = 0; i < indices.length; ++i) {
			indices[i] = random.nextInt(stringLength);
		}
		
		String[] expected = StringUtil.splitAt(src, indices);
		
		for (int i = 0; i < 10000; ++i) {
			shuffleArray(indices, random);
			
			int[] copy = indices.clone();
			test(src, indices, expected);
			assertArrayEquals(indices, copy); // Make sure indices array hasn't changed
		}
	}
	
	// Shamelessly copied from
	//   https://stackoverflow.com/a/1520212/4463352
	// Thanks, https://stackoverflow.com/users/15459/philho!
	
	// Implementing Fisher–Yates shuffle
	private static void shuffleArray(int[] ar, Random random) {
		for (int i = ar.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	private void test(String string, int[] at, String[] expecteds) {
		assertArrayEquals(expecteds, StringUtil.splitAt(string, at));
	}

}
