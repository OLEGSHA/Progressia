package ru.windcorp.progressia.client.audio;

public class ALTest {
	static private void initializeAL() {
		AudioManager.initAL();
	}

	static void loadALData() {
		AudioManager.loadSound("assets/sounds/sample_stereo.ogg",
				"Progressia", "SampleStereo",
				AudioFormat.STEREO);
		Music music  = new Music("Progressia", "SampleStereo");
		music.play(false);
	}

	static void killALData() {
		//TODO implement the method or its analogue
	}

	public static void execute() {
		initializeAL();
		loadALData();
	}
}