package ru.windcorp.progressia.client.audio;

public class AudioSystem {
	static public void initialize() {
		AudioManager.initAL();
		Thread shutdownHook = new Thread(AudioManager::closeAL, "AL Shutdown Hook");
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		loadAudioData();
	}

	static void loadAudioData() {
		AudioManager.loadSound("assets/sounds/block_destroy_clap.ogg",
				"Progressia:BlockDestroy",
				AudioFormat.MONO);
	}
}