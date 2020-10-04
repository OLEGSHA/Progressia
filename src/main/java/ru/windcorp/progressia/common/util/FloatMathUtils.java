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
	
	public static float sin(float x) {
		return (float) Math.sin(x);
	}
	
	public static float cos(float x) {
		return (float) Math.cos(x);
	}
	
	public static float tan(float x) {
		return (float) Math.tan(x);
	}
	
	private FloatMathUtils() {}

}
