package ru.windcorp.progressia.common.state;

class PrimitiveCounters {
	
	private int ints = 0;
	
	public PrimitiveCounters() {}
	
	public PrimitiveCounters(PrimitiveCounters copyFrom) {
		this.ints = copyFrom.ints;
	}
	
	public int getInts() {
		return ints;
	}
	
	public int getIntsThenIncrement() {
		return this.ints++;
	}

}
