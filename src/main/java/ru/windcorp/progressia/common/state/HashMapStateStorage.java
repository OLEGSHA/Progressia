package ru.windcorp.progressia.common.state;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class HashMapStateStorage extends StateStorage {
	
	private final TIntIntMap ints = new TIntIntHashMap();

	@Override
	public int getInt(int index) {
		return ints.get(index);
	}

	@Override
	public void setInt(int index, int value) {
		ints.put(index, value);
	}

}
