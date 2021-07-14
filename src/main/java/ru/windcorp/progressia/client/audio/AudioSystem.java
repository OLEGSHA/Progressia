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

package ru.windcorp.progressia.client.audio;

import org.apache.logging.log4j.LogManager;
import ru.windcorp.progressia.common.modules.Module;
import ru.windcorp.progressia.common.modules.Task;
import ru.windcorp.progressia.common.modules.TaskManager;
import ru.windcorp.progressia.common.resource.ResourceManager;

public class AudioSystem {
	static public void initialize() {
		Module audioModule = new Module("AudioModule:System");
		AudioManager.initAL();
		Thread shutdownHook = new Thread(AudioManager::closeAL, "AL Shutdown Hook");
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		Task t = new Task("AudioSystem:Initialize") {
			@Override
			protected void perform() {
				loadAudioData();
				LogManager.getLogger().info("Audio data is loaded");
			}
		};
		audioModule.addTask(t);
		TaskManager.getInstance().registerModule(audioModule);
	}

	static void loadAudioData() {
		AudioManager.loadSound(
				ResourceManager.getResource("assets/sounds/block_destroy_clap.ogg"),
				"Progressia:BlockDestroy",
				AudioFormat.MONO
		);
	}
}
