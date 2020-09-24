package ru.windcorp.progressia.client.audio.backend;

import ru.windcorp.progressia.client.audio.AudioReader;
import ru.windcorp.progressia.client.audio.Sound;
import ru.windcorp.progressia.client.audio.SoundManager;

public class ALTest {
	static private void initializeAL() {
		SoundManager.initAL();
	}

	static void loadALData() {
		Sound music = SoundManager.createSound(AudioReader.readAsMono("assets/sounds/sample_mono.ogg"));
		music.playOnce();
		/*music = SoundManager.createSound(AudioReader.readAsStereo("assets/sounds/sample_mono.ogg"));
		music.playOnce();*/
	}

	static void killALData() {
		//TODO implement the method or its analogue
	}

	public static void execute() {
		initializeAL();
		loadALData();
	}
}