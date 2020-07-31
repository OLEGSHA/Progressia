package ru.windcorp.optica.common.util;

public class BinUtil {
	
	public static int closestGreaterPowerOf2(int x) {
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return x + 1;
	}
	
	public static int roundToGreaterPowerOf2(int x) {
		return closestGreaterPowerOf2(x - 1);
	}

}
