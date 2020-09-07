package ru.windcorp.progressia.common.util;

import glm.vec._2.Vec2;
import glm.vec._2.i.Vec2i;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import glm.vec._4.i.Vec4i;

/**
 * A set of caches for GLM vector objects. Use this instead of allocating new
 * vectors when the objects are effectively local.
 * <p>
 * All {@code grab}bed objects must be {@code release}d as soon as possible.
 * Ideally, user code should be:
 * <pre>
 * Vec3 myVector = Vectors.grab3();
 * try {
 *     // use myVector
 * } finally {
 *     Vectors.release(myVector);
 * }
 * </pre>
 * Provided objects may be reused after {@code release} has been invoked;
 * do not store them.
 * <p>
 * This class is thread- and recursion-safe.
 * 
 * @see Matrices
 */
public class Vectors {
	
	private static final LowOverheadCache<Vec3i> VEC3IS =
			new LowOverheadCache<>(Vec3i::new);
	
	public static Vec3i grab3i() {
		return VEC3IS.grab();
	}
	
	public static void release(Vec3i v) {
		VEC3IS.release(v);
	}
	
	private static final LowOverheadCache<Vec3> VEC3S =
			new LowOverheadCache<>(Vec3::new);
	
	public static Vec3 grab3() {
		return VEC3S.grab();
	}
	
	public static void release(Vec3 v) {
		VEC3S.release(v);
	}
	
	private static final LowOverheadCache<Vec2i> VEC2IS =
			new LowOverheadCache<>(Vec2i::new);
	
	public static Vec2i grab2i() {
		return VEC2IS.grab();
	}
	
	public static void release(Vec2i v) {
		VEC2IS.release(v);
	}
	
	private static final LowOverheadCache<Vec2> VEC2S =
			new LowOverheadCache<>(Vec2::new);
	
	public static Vec2 grab2() {
		return VEC2S.grab();
	}
	
	public static void release(Vec2 v) {
		VEC2S.release(v);
	}
	
	private static final LowOverheadCache<Vec4i> VEC4IS =
			new LowOverheadCache<>(Vec4i::new);
	
	public static Vec4i grab4i() {
		return VEC4IS.grab();
	}
	
	public static void release(Vec4i v) {
		VEC4IS.release(v);
	}
	
	private static final LowOverheadCache<Vec4> VEC4S =
			new LowOverheadCache<>(Vec4::new);
	
	public static Vec4 grab4() {
		return VEC4S.grab();
	}
	
	public static void release(Vec4 v) {
		VEC4S.release(v);
	}
	
	private Vectors() {}

}
