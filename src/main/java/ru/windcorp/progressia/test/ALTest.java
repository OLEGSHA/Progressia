package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.audio.AudioFormat;
import ru.windcorp.progressia.client.audio.AudioManager;
import ru.windcorp.progressia.client.audio.Music;

public class ALTest {
	static private void initializeAL() {
		AudioManager.initAL();
	}

	static void loadALData() {
		AudioManager.loadSound("assets/sounds/sample_stereo.ogg",
				"Progressia:SampleStereo",
				AudioFormat.STEREO);
		AudioManager.loadSound("assets/sounds/block_destroy_clap.ogg",
				"Progressia:BlockDestroy",
				AudioFormat.MONO);
		Music music  = new Music("Progressia:SampleStereo");
		music.setGain(0.5f);
		//music.play(false);
	}

	public static void execute() {
		initializeAL();
		Thread shutdownHook = new Thread(AudioManager::closeAL, "AL Shutdown Hook");
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		loadALData();
	}
}