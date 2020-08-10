/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics.input;

import gnu.trove.set.TIntSet;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;

public class KeyMatcher {
	
	private final int key;
	private final int[] additionalKeys;
	private final int mods;
	
	public KeyMatcher(int key, int[] additionalKeys, int mods) {
		this.key = key;
		this.additionalKeys = additionalKeys;
		this.mods = mods;
	}
	
	public KeyMatcher(KeyEvent template, int... additionalKeys) {
		this(template.getKey(), additionalKeys, template.getMods());
	}
	
	public static KeyMatcher createKeyMatcher(KeyEvent template) {
		return new KeyMatcher(
				template,
				InputTracker.getPressedKeys().toArray()
		);
	}

	public boolean matches(KeyEvent event) {
		if (!event.isPress()) return false;
		if (event.getKey() != getKey()) return false;
		if (event.getMods() != getMods()) return false;
		
		TIntSet pressedKeys = InputTracker.getPressedKeys();
		
		if (pressedKeys.size() != additionalKeys.length) return false;
		
		for (int additionalKey : additionalKeys) {
			if (!pressedKeys.contains(additionalKey)) {
				return false;
			}
		}
		
		return true;
	}
	
	public int getKey() {
		return key;
	}
	
	public int[] getAdditionalKeys() {
		return additionalKeys;
	}
	
	public int getMods() {
		return mods;
	}

}
