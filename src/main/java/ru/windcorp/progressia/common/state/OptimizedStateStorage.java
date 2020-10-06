package ru.windcorp.progressia.common.state;

public class OptimizedStateStorage extends StateStorage {
	
	private final int[] ints;
	
	public OptimizedStateStorage(PrimitiveCounters sizes) {
		this.ints = new int[sizes.getInts()];
	}

	public int getInt(int index) {
		return ints[index];
	}

	public void setInt(int index, int value) {
		ints[index] = value;
	}

}
