/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
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
 */
package ru.windcorp.progressia.common.world.generic;

import static org.junit.Assert.fail;
import static ru.windcorp.progressia.common.world.generic.GenericChunks.*;

import java.util.Random;

import org.junit.Test;

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.rels.AbsFace;

public class GenericChunkRotationsTest {
	
	private static void assertVecEquals(String message, AbsFace up, Vec3i a, Vec3i b) {
		if (a == b) {
			return;
		}
		
		if (Glm.equals(a, b)) {
			return;
		}
		
		fail(String.format("%s. x = (%4d; %4d; %4d), got (%4d; %4d; %4d) (up = %s)", message, a.x, a.y, a.z, b.x, b.y, b.z, up));
	}
	
	private void check(int x, int y, int z) {
		for (AbsFace up : AbsFace.getFaces()) {
		
			Vec3i veci = new Vec3i(x, y, z);
			
			assertVecEquals("Vec3i, x != resolve(relativize(x))", up, veci, resolve(relativize(veci, up, null), up, null));
			assertVecEquals("Vec3i, x != relativize(resolve(x))", up, veci, relativize(resolve(veci, up, null), up, null));
		
		}
	}
	
	@Test
	public void specialCases() {
		
		for (int x = -1; x <= 1; ++x) {
			for (int y = -1; y <= 1; ++y) {
				for (int z = -1; z <= 1; ++z) {
					check(x, y, z);
				}
			}
		}
		
	}
	
	@Test
	public void randomValues() {
		
		final int iterations = 2 << 16;
		final long seed = 0;
		
		Random random = new Random(seed);
		
		for (int i = 0; i < iterations; ++i) {
			check(random.nextInt(200) - 100, random.nextInt(200) - 100, random.nextInt(200) - 100);
		}
		
	}

}
