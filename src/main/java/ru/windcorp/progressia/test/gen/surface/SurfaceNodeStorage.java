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
package ru.windcorp.progressia.test.gen.surface;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.world.DecodingException;

public class SurfaceNodeStorage {
	
	public static class Node {
//		private float[] floats;
	}
	
	private final TLongObjectMap<Node> map = new TLongObjectHashMap<>();
	
	public Node getNode(int north, int west) {
		return map.get(CoordinatePacker.pack2IntsIntoLong(north, west));
	}
	
	public boolean hasNode(int north, int west) {
		return map.containsKey(CoordinatePacker.pack2IntsIntoLong(north, west));
	}
	
	public void put(int north, int west, Node node) {
		map.put(CoordinatePacker.pack2IntsIntoLong(north, west), node);
	}
	
	public void read(DataInput input) throws IOException, DecodingException {
		System.err.println("PlaneNodeMap.read did nothing because nobody implemented it yet");
	}
	
	public void write(DataOutput output) throws IOException {
		System.err.println("PlaneNodeMap.write did nothing because nobody implemented it yet");
	}

}
