package ru.windcorp.optica.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ru.windcorp.optica.common.util.CoordinatePacker;

public class CoordinatePackerTest {
	
	@Test
	public void cornerCases() {
		check(0, 0, 0);
		check(0, 0, 42);
		check(0, 42, 0);
		check(42, 0, 0);
		check(1, 1, 1);
		check(-1, -1, -1);
		check(1 << 19, 1 << 19, 1 << 19);
		check((1 << 20) - 1, (1 << 20) - 1, (1 << 20) - 1);
		check(-(1 << 19), -(1 << 19), -(1 << 19));
	}
	
	@Test
	public void randomValues() {
		Random random = new Random(0);
		int bound = 1 << 20;
		
		for (int i = 0; i < 1000000; ++i) {
			check(
					random.nextInt(bound) * (random.nextBoolean() ? 1 : -1),
					random.nextInt(bound) * (random.nextBoolean() ? 1 : -1),
					random.nextInt(bound) * (random.nextBoolean() ? 1 : -1)
			);
		}
	}

	private void check(int a, int b, int c) {
		
		long packed = CoordinatePacker.pack3IntsIntoLong(a, b, c);
		
		int unpackedA = CoordinatePacker.unpack3IntsFromLong(packed, 0);
		int unpackedB = CoordinatePacker.unpack3IntsFromLong(packed, 1);
		int unpackedC = CoordinatePacker.unpack3IntsFromLong(packed, 2);
		
		assertEquals(a, unpackedA);
		assertEquals(b, unpackedB);
		assertEquals(c, unpackedC);
		
	}

}
