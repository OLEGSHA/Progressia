package ru.windcorp.progressia.server;

import ru.windcorp.progressia.common.Units;

public class TickingSettings {
	
	private float randomTickFrequency = Units.get("1 min^-1");
	
	/**
	 * Returns the average rate of random ticks in a single block.
	 * @return ticking frequency
	 */
	public float getRandomTickFrequency() {
		return randomTickFrequency;
	}

}
