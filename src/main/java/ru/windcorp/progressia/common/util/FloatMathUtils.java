package ru.windcorp.progressia.common.util;

import org.apache.commons.math3.util.FastMath;

public class FloatMathUtils {
	
	public static final float PI_F = (float) Math.PI;
	
	public static float floor(float x) {
		return (float) FastMath.floor(x);
	}
	
	public static float normalizeAngle(float a) {
		return a - 2*PI_F * floor((a + PI_F) / (2*PI_F));
	}
	
	private FloatMathUtils() {}

}
