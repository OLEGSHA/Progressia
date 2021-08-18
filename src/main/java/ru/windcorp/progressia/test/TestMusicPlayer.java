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
package ru.windcorp.progressia.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;

import ru.windcorp.progressia.client.audio.AudioFormat;
import ru.windcorp.progressia.client.audio.AudioManager;
import ru.windcorp.progressia.client.audio.AudioRegistry;
import ru.windcorp.progressia.client.audio.Music;
import ru.windcorp.progressia.client.audio.Sound;
import ru.windcorp.progressia.client.audio.backend.SoundType;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class TestMusicPlayer implements Runnable {

	private static final int MIN_SILENCE = 15 * 1000; // 15 seconds
	private static final int MAX_SILENCE = 60 * 1000; // one minute

	private static TestMusicPlayer instance = null;

	private final List<SoundType> compositions = new ArrayList<>();

	private final Random random = new Random();
	private long nextStart;
	private Sound lastStarted = null;

	public TestMusicPlayer() {
		this.nextStart = System.currentTimeMillis();

		instance = this;
	}

	public static void start() {
		Thread thread = new Thread(new TestMusicPlayer(), "Music Thread");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		loadCompositions();

		if (compositions.isEmpty()) {
			LogManager.getLogger().warn("No music found");
			return;
		}

		while (true) {

			try {
				synchronized (this) {
					while (true) {
						long now = System.currentTimeMillis();
						if (nextStart > now) {
							wait(nextStart - now);
						} else {
							break;
						}
					}
				}
			} catch (InterruptedException e) {
				LogManager.getLogger().warn("Received interrupt in music thread, terminating thread...");
				return;
			}

			startNextComposition();

		}
	}

	private void loadCompositions() {
		try {

			Path directory = Paths.get("music");

			if (!Files.isDirectory(directory)) {
				Files.createDirectories(directory);
			}

			Iterator<Path> it = Files.walk(directory).filter(Files::isRegularFile).iterator();
			int i = 0;

			while (it.hasNext()) {
				String file = it.next().toString();
				if (!file.endsWith(".ogg") && !file.endsWith(".oga")) {
					LogManager.getLogger().warn("Skipping " + file + ": not .ogg nor .oga");
				}

				String id = "Progressia:Music" + (i++);

				AudioManager.loadSound(ResourceManager.getFileResource(file.toString()), id, AudioFormat.STEREO);
				SoundType composition = AudioRegistry.getInstance().get(id);
				compositions.add(composition);

				LogManager.getLogger().info("Loaded " + file);
			}

		} catch (IOException e) {
			throw CrashReports.report(e, "Could not load music");
		}
	}

	private synchronized void startNextComposition() {
		int index = random.nextInt(compositions.size());
		SoundType composition = compositions.get(index);

		long now = System.currentTimeMillis();
		long durationInMs = (long) (composition.getDuration() * 1000);
		long silence = random.nextInt(MAX_SILENCE - MIN_SILENCE) + MIN_SILENCE;

		nextStart = now + durationInMs + silence;

		lastStarted = new Music(composition);
		lastStarted.play(false);
	}

	public static void startNextNow() {
		if (instance == null)
			return;

		synchronized (instance) {
			instance.nextStart = System.currentTimeMillis();
			instance.notifyAll();
		}
	}

}
