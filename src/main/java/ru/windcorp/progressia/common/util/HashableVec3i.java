package ru.windcorp.progressia.common.util;

import glm.vec._3.i.Vec3i;

public class HashableVec3i {

	public Vec3i value;
	
	public HashableVec3i(Vec3i inValue)
	{
		value = inValue;
	}
	
	@Override
	public int hashCode() // Uses first 3 primes greater than 2**30
	{
		return 1073741827 * value.x + 1073741831 * value.y + 1073741833 * value.z;
	}
	
	@Override
	public boolean equals(Object comparee)
	{
		if (comparee == null)
		{
			return false;
		}
		if (comparee.getClass() != HashableVec3i.class)
		{
			return false;
		}
		HashableVec3i compareeCast = (HashableVec3i) comparee;
		return compareeCast.value == value;
	}
	
}
