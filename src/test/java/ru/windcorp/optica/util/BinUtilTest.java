package ru.windcorp.optica.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ru.windcorp.optica.common.util.BinUtil;

public class BinUtilTest {
	
	@Test
	public void cornerCases() {
		test(1);
		test(2);
		test(3);
		test(4);
		test(7);
		test(8);
		test(9);
		
		test(1023);
		test(1024);
		test(1025);
		
		test((1 << 16) - 1);
		test(1 << 16);
		test((1 << 16) + 1);
	}
	
	@Test
	public void random() {
		Random random = new Random(0);
		
		for (int i = 0; i < 10000; ++i) {
			test(random.nextInt((1 << 30) - 2) + 1);
		}
	}
	
	void test(int x) {
		assertEquals("Round, x = " + x, referenceRound(x), BinUtil.roundToGreaterPowerOf2(x));
		assertEquals("Greater, x = " + x, referenceGreater(x), BinUtil.closestGreaterPowerOf2(x));
	}
	
	int referenceGreater(int x) {
		int p;
		for (p = 1; p <= x; p *= 2);
		return p;
	}
	
	int referenceRound(int x) {
		int p;
		for (p = 1; p < x; p *= 2);
		return p;
	}

}
